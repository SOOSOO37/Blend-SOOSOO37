package com.blend.server.test;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.product.ProductService;
import com.blend.server.productImage.ProductImage;
import com.blend.server.review.Review;
import com.blend.server.seller.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.util.*;

import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }
    @DisplayName("상품 생성 테스트")
    @Test
    public void createProductTest() throws IOException {
        Seller seller = new Seller();
        seller.setId(1L);

        Category category = TestObjectFactory.createCategory();

        Product product = TestObjectFactory.createProduct();
        product.setSeller(seller);

        List<ProductImage> productImages = new ArrayList<>();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(product, 1L,productImages,seller);

        assertNotNull(createdProduct);
        assertEquals(category, createdProduct.getCategory());
        assertEquals(product,createdProduct);

    }

    @DisplayName("상품 수정 테스트")
    @Test
    public void UpdateProductTest() {
        Seller seller = new Seller();
        seller.setId(1L);

        Category category = TestObjectFactory.createCategory();

        Product product = TestObjectFactory.createProduct();
        product.setCategory(category);
        product.setSeller(seller);

        long categoryId = 2L;
        Category category1 = new Category();
        category1.setId(categoryId);
        category1.setName("하의");

        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setProductName("수정 제품명");
        updatedProduct.setBrand("수정 브랜드");
        updatedProduct.setCategory(category1);
        updatedProduct.setPrice(50000);
        updatedProduct.setSalePrice(45000);
        updatedProduct.setProductStatus(Product.ProductStatus.SALE);
        updatedProduct.setReviewCount(0);
        updatedProduct.setLikeCount(0);
        updatedProduct.setProductCount(15);
        updatedProduct.setInfo("수정 제품 정보");
        updatedProduct.setSizeInfo("수정 사이즈 정보");


        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.findByIdAndSeller(product.getId(),seller)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));

        Product updated = productService.updateProduct(seller,product.getId(), updatedProduct, categoryId);

        assertNotNull(updated);
        assertEquals(updatedProduct.getId(), updated.getId());
        assertEquals("수정 제품명", updated.getProductName());
        assertEquals("수정 브랜드", updated.getBrand());
        assertEquals(updatedProduct.getCategory(), updated.getCategory());
        assertEquals(50000, updated.getPrice());
        assertEquals(45000, updated.getSalePrice());
        assertEquals(Product.ProductStatus.SALE, updated.getProductStatus());
        assertEquals(0, updated.getReviewCount());
        assertEquals(0, updated.getLikeCount());
        assertEquals(15, updated.getProductCount());
        assertEquals("수정 제품 정보", updated.getInfo());
        assertEquals("수정 사이즈 정보", updated.getSizeInfo());

    }

    @DisplayName("상품 상태 수정")
    @Test
    public void testUpdateStatus() {
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = TestObjectFactory.createProduct();
        product.setProductCount(4);
        product.setSeller(seller);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.findByIdAndSeller(product.getId(),seller)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateStatus(product.getId(),seller,product);

        assertNotNull(updatedProduct);

        assertEquals(Product.ProductStatus.INSTOCK, updatedProduct.getProductStatus());

    }

    @DisplayName("상품 조회 및 조회수,리뷰 수 증가 테스트")
    @Test
    public void findProductTest() {

        Product product = TestObjectFactory.createProduct();
        product.setViewCount(0);

        List<Review> reviews = new ArrayList<>();
        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);
        reviews.add(review);
        product.setReviews(reviews);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product findProduct = productService.findProduct(product.getId());

        assertNotNull(findProduct);
        assertEquals(1, findProduct.getViewCount());
        assertEquals(1, findProduct.getReviewCount());
    }

    @DisplayName("상품조회 예외 테스트")
    @Test
    public void findProductExceptionTest() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> {
            productService.findProduct(1L);
        });
    }

    @DisplayName("실시간 랭킹 조회 테스트")
    @Test
    public void findProductRanksTest() {

        List<Product> productList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(i);
            product.setViewCount(i * 10);
            productList.add(product);
        }
        int page = 0;
        int size = 5;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("viewCount").descending());

        List<Product> productLists = productList.subList(0, size);

        when(productRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(productLists, pageRequest, productList.size()));

        Page<Product> products = productService.findProductRanks(page, size);

        assertNotNull(products);
        assertEquals(size, products.getNumberOfElements());
        assertEquals(10, products.getTotalElements());
        assertEquals(10, products.getContent().get(0).getViewCount());
    }

    @DisplayName("카테고리 검색 테스트")
    @Test
    public void CategoryTest() {
        Category category = TestObjectFactory.createCategory();

        List<Product> productList1 = new ArrayList<>();
        Product product = TestObjectFactory.createProduct();
        product.setCategory(category);
        productList1.add(product);

        Product product2 = TestObjectFactory.createProduct();
        product2.setProductName("제품명2");
        product2.setCategory(category);
        productList1.add(product2);

        Category category2 = TestObjectFactory.createCategory();
        category2.setId(2);
        category2.setName("하의");

        List<Product> productList2 = new ArrayList<>();
        Product product3 = TestObjectFactory.createProduct();
        product3.setProductName("제품명3");
        product3.setCategory(category2);
        productList2.add(product3);

        when(categoryRepository.findByName("상의")).thenReturn(category);
        when(categoryRepository.findByName("하의")).thenReturn(category2);

        when(productRepository.findByCategory(eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(productList1));
        when(productRepository.findByCategory(eq(category2), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(productList2));

        int page = 0;
        int size = 5;
        String name = "상의";
        Page<Product> result = productService.findCategory(page, size, name);

        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements()); // 페이지 크기 확인
        assertEquals(2, result.getTotalElements()); // 전체 제품 수 확인
        assertEquals("제품명", result.getContent().get(0).getProductName());
        assertEquals("제품명2", result.getContent().get(1).getProductName());
        assertEquals("상의", result.getContent().get(0).getCategory().getName());
        assertEquals("상의", result.getContent().get(1).getCategory().getName());

        int page1 = 0;
        int size1 = 5;
        String name1 = "하의";
        Page<Product> result1 = productService.findCategory(page1, size1, name1);

        assertNotNull(result1);
        assertEquals(1, result1.getNumberOfElements()); // 페이지 크기 확인
        assertEquals(1, result1.getTotalElements()); // 전체 제품 수 확인
        assertEquals("제품명3", result1.getContent().get(0).getProductName());
        assertEquals("하의", result1.getContent().get(0).getCategory().getName());
    }

    @DisplayName("판매중인 상품 조회")
    @Test
    public void SaleProductTest () {

        Product product = TestObjectFactory.createProduct();
        product.setProductName("제품명");
        product.setProductStatus(Product.ProductStatus.SALE);

        Product product2 = TestObjectFactory.createProduct();
        product2.setProductName("2제품명");
        product2.setProductStatus(Product.ProductStatus.INSTOCK);

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);

        when(productRepository.findByProductStatusIn((Set.of(Product.ProductStatus.SALE, Product.ProductStatus.INSTOCK)), pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(product),pageable,1));

        Page<Product> result = productService.findSaleProduct(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("제품명", result.getContent().get(0).getProductName());
        assertEquals(Product.ProductStatus.SALE, result.getContent().get(0).getProductStatus());
    }

    @DisplayName("상품 삭제 테스트")
    @Test
    public void deleteProductTest() {
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = TestObjectFactory.createProduct();
        product.setSeller(seller);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.findByIdAndSeller(product.getId(),seller)).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId(),seller);

    }

    @DisplayName("상품 존재 확인 테스트")
    @Test
    public void findVerifiedTest() {

        Product product = TestObjectFactory.createProduct();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product findProduct = productService.findVerifiedProduct(product.getId());

        assertNotNull(findProduct);
        assertEquals(product.getId(),findProduct.getId());
        assertEquals("제품명",findProduct.getProductName());

        long productId = 2;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> productService.findVerifiedProduct(productId));

    }
}