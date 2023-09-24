package com.blend.server.Product;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public Product createProduct(Product product, long categoryId) {
        log.info("---Creating Product---");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        log.info("Product created: {}", product);

        return productRepository.save(product);
    }

    public Product updateProduct(long id,Product product,long categoryId) {
        log.info("--- Updating Product Id: {} ---",id);

        Product findProduct = findVerifiedProduct(id);

        log.info("Updating Product: {}",product);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        BeanUtils.copyProperties(product, findProduct,"productStatus");

        log.info("Updated Product: {}",findProduct);

        return productRepository.save(findProduct);
    }

    public Product updateStatus(long id){
        log.info("---Updating Status---", id);

        Product findProduct = findVerifiedProduct(id);

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

        Pageable pageable = PageRequest.of(page,size);
        return productRepository.findByProductStatus(Product.ProductStatus.SALE, pageable);

    }

    public void deleteProduct(long id){
        log.info("Deleting Product", id);

        Product findProduct = findVerifiedProduct(id);

        productRepository.deleteById(id);

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


}
