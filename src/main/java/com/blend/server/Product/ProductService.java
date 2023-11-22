package com.blend.server.Product;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.productImage.ProductImage;
import com.blend.server.productImage.ProductImageRepository;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final SellerRepository sellerRepository;

    private final SellerService sellerService;

    private final ProductImageRepository productImageRepository;

    public Product createProduct(Product product, long categoryId,List<ProductImage> productImages,Seller seller) {
        log.info("---Creating Product---");

        if (seller!=null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
            // 이미지 추가
            if (productImages != null) {
                List<ProductImage> productImageList = productImages.stream()
                        .map(image -> {
                            product.addProductImage(image);
                            return image;
                        })
                        .collect(Collectors.toList());
            }
            return productRepository.save(product);
        }
        throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
    }

    public Product updateProduct (Seller seller, long productId, Product product, long categoryId) {
        log.info("--- Updating Product for Seller ID: {}, Product ID: {} ---", seller, productId);

        verifySeller(productId, seller.getId());
        Product findProduct = findProductBySeller(productId, seller);

        log.info("Updating Product: {}", product);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        BeanUtils.copyProperties(product, findProduct, "productStatus","seller","imageLinks","productImages");

        log.info("Updated Product: {}", findProduct);

        return productRepository.save(findProduct);
    }

    private Product findProductBySeller(long productId, Seller seller) {
        return productRepository.findByIdAndSeller(productId, seller)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
    }

    public Product updateStatus(long id,Seller seller,Product product){
        log.info("---Updating Status---", id);
        verifySeller(id, seller.getId());
        Product findProduct = findProductBySeller(id, seller);

        log.info("Current status: {}",findProduct.getProductStatus());

        changeProductStatus(findProduct);

        log.info("Updated status: {}",findProduct.getProductStatus());

        return productRepository.save(findProduct);

    }


    public Product findProduct(long id){
        log.info("---Inquiring Product---",id);
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setViewCount(product.getViewCount()+1);
            this.productRepository.save(product);
            log.info("Find Product: {}",product);
            return product;
        }else {
            log.warn("---Product Not Found---",id);
            throw new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
        }

    }

    public Page<Product> findProductRanks(int page, int size){
        log.info("---Inquiring Ranking---");

        return productRepository.findAll(PageRequest.of(page,size, Sort.by("viewCount").descending()));

    }

    public Page<Product> findCategory(int page, int size, String name){
        log.info("---Inquiring Category---");

        Category category = categoryRepository.findByName(name);

        return productRepository.findByCategory(category, PageRequest.of(page, size, Sort.by("id").descending()));

    }

    public Page<Product> findSaleProduct (int page, int size){
        log.info("---Searching On Sale Products---");

        Set<Product.ProductStatus> saleStatuses = Set.of(Product.ProductStatus.SALE, Product.ProductStatus.INSTOCK);
        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findByProductStatusIn(saleStatuses, pageable);
        }

    public void deleteProduct(long id,Seller seller){
        log.info("Deleting Product", id);
        verifySeller(id, seller.getId());
        Product findProduct = findProductBySeller(id,seller);

        productRepository.deleteById(findProduct.getId());

        log.info("Deleted Product {}",id);
    }


    public Product findVerifiedProduct(long id){
        log.info("Verifying Product", id);

        Optional<Product> optionalProduct =
                productRepository.findById(id);
        Product findProduct =
                optionalProduct.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));

        log.info("Verified product: {}",findProduct);

        return findProduct;

    }

    public void changeProductStatus(Product product) {
        if (ProductCountRange(product.getProductCount())) {
            product.setProductStatus(Product.ProductStatus.INSTOCK);
        } else if (product.getProductCount() == 0) {
            product.setProductStatus(Product.ProductStatus.SOLDOUT);
        }
    }

    private boolean ProductCountRange(int productCount) {
        return productCount >= 1 && productCount <= 5;
    }

    public void verifySeller(long productId, long sellerId){
        Product findProduct = findVerifiedProduct(productId);
        long dbSellerId = findProduct.getSeller().getId();

        if(sellerId != dbSellerId){
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
    }


}
