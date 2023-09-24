package com.blend.server.test;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.Product.Product;
import com.blend.server.Product.ProductRepository;
import com.blend.server.Product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 모의 객체를 초기화하고 의존성 주입 (현재 테스트 클래스의 인스턴스를 초기화)
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("상품 생성 테스트")
    @Test
    public void createProductTest() {
        // 샘플 카테고리 생성
        Category category = TestObjectFactory.createCategory();

        // 샘플 상품 생성
        Product product = TestObjectFactory.createProduct();

        // 카테고리 레포지토리에서 메서드 호출 시 Id가 1인 것을 찾으면 이 카테고리를 리턴
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        // 객체를 저장하고 저장된 객체 리턴
        when(productRepository.save(product)).thenReturn(product);
        //테스트 서비스메소드 호출
        Product createdProduct = productService.createProduct(product, 1L);

        // 생성된 상품 확인(검증)
        assertNotNull(createdProduct);
        assertEquals(category, createdProduct.getCategory());
        assertEquals(product,createdProduct);

    }

    @DisplayName("상품 수정 테스트")
    @Test
    public void testUpdateProduct() {
        // 카테고리 생성
        Category category = TestObjectFactory.createCategory();

        // 상품 생성
        Product product = TestObjectFactory.createProduct();
        product.setCategory(category);

        // 카테고리 생성 (해당 카테고리로 상품 카테고리 수정을 위해)
        long categoryId = 2L;
        Category category1 = new Category();
        category1.setId(categoryId);
        category1.setName("하의");

        // 상품 수정
        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setProductName("수정 제품명");
        updatedProduct.setBrand("수정 브랜드");
        updatedProduct.setCategory(category1);
        updatedProduct.setPrice(50000);
        updatedProduct.setSalePrice(45000);
        updatedProduct.setProductStatus(Product.ProductStatus.SALE);
        updatedProduct.setImage("Image2.PNG");
        updatedProduct.setReviewCount(0);
        updatedProduct.setLikeCount(0);
        updatedProduct.setProductCount(15);
        updatedProduct.setInfo("수정 제품 정보");
        updatedProduct.setSizeInfo("수정 사이즈 정보");


        // 메서드 호출 시 해당 Id를 찾으면 해당 객체를 리턴
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        //항상 동일한 객체를 리턴
        //when(productRepository.save(any(Product.class))).thenReturn(product);

        //save 메서드 호출 시 실제 db에 저장 X 테스트에 전달한 객체를 그대로 반환(입력과 동시에 출력 반환 -> 메서드 동작 시뮬레이션 가능) 정교한 테스트
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return savedProduct;
        });

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));


        // ProductService의 updateProduct 메서드 호출
        Product updated = productService.updateProduct(product.getId(), updatedProduct, categoryId);

        // 제품이 업데이트되었는지 확인
        assertNotNull(updated);
        assertEquals(updatedProduct.getId(), updated.getId());
        assertEquals("수정 제품명", updated.getProductName());
        assertEquals("수정 브랜드", updated.getBrand());
        assertEquals(updatedProduct.getCategory(), updated.getCategory());
        assertEquals(50000, updated.getPrice());
        assertEquals(45000, updated.getSalePrice());
        assertEquals(Product.ProductStatus.SALE, updated.getProductStatus());
        assertEquals("Image2.PNG", updated.getImage());
        assertEquals(0, updated.getReviewCount());
        assertEquals(0, updated.getLikeCount());
        assertEquals(15, updated.getProductCount());
        assertEquals("수정 제품 정보", updated.getInfo());
        assertEquals("수정 사이즈 정보", updated.getSizeInfo());

    }

    @DisplayName("상품 상태 수정")
    @Test
    public void testUpdateStatus() {

        Product product = TestObjectFactory.createProduct();
        product.setProductCount(4);

        //항상 동일한 객체 리턴 save 메서드 -> 객체저장
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //해당 Id값 객체 조회 반환
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product updatedProduct = productService.updateStatus(1);

        assertNotNull(updatedProduct);

        assertEquals(Product.ProductStatus.INSTOCK, updatedProduct.getProductStatus());

    }

    @DisplayName("상품 조회 및 조회수 증가 테스트")
    @Test
    public void findProductTest() {

        Product product = TestObjectFactory.createProduct();
        product.setViewCount(0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product findProduct = productService.findProduct(1L);

        assertNotNull(findProduct);

        assertEquals(1, findProduct.getViewCount());

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
        // 첫 번째 카테고리와 상품 객체 생성
        Category category = TestObjectFactory.createCategory();

        List<Product> productList1 = new ArrayList<>();
        Product product = TestObjectFactory.createProduct();
        product.setCategory(category);
        productList1.add(product);

        Product product2 = TestObjectFactory.createProduct();
        product2.setProductName("제품명2");
        product2.setCategory(category);
        productList1.add(product2);

        // 두 번째 카테고리와 상품 객체 생성
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

        // eq매처를 사용하여 메서드에 전달되는 인자가 category인지 확인 일치해야만 동작 수행
        when(productRepository.findByCategory(eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(productList1));
        when(productRepository.findByCategory(eq(category2), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(productList2));

        // 상의 카테고리 조회
        int page = 0;
        int size = 5;
        String name = "상의";
        Page<Product> result = productService.findCategory(page, size, name);

        // 검증
        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements()); // 페이지 크기 확인
        assertEquals(2, result.getTotalElements()); // 전체 제품 수 확인
        assertEquals("제품명", result.getContent().get(0).getProductName());
        assertEquals("제품명2", result.getContent().get(1).getProductName());
        assertEquals("상의", result.getContent().get(0).getCategory().getName());
        assertEquals("상의", result.getContent().get(1).getCategory().getName());

        // 하의 카테고리 조회
        int page1 = 0;
        int size1 = 5;
        String name1 = "하의";
        Page<Product> result1 = productService.findCategory(page1, size1, name1);

        // 검증
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

        when(productRepository.findByProductStatus(Product.ProductStatus.SALE, pageable))
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

        Product product = TestObjectFactory.createProduct();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).deleteById(product.getId());

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