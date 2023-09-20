package com.blend.server.Product.product;

import com.blend.server.Product.category.Category;
import com.blend.server.Product.category.CategoryRepository;
import com.blend.server.Product.global.exception.BusinessLogicException;
import com.blend.server.Product.global.exception.ExceptionCode;
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
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(Product product, long categoryId) {
        log.info("---Creating Product---");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product updateProduct(long id,Product product,long categoryId) {
        log.info("---Updating Product---");

        Product findProduct = findVerifiedProduct(id);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));

        product.setCategory(category);

        BeanUtils.copyProperties(product, findProduct,"productStatus");

        return productRepository.save(findProduct);
    }

    public Product updateStatus(long id){
        log.info("---Updating Status---", id);

        Product findProduct = findVerifiedProduct(id);

        changeProductStatus(findProduct);

        return productRepository.save(findProduct);

    }

    public Product findProduct(long id){
        log.info("---Inquiring Product---",id);
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setViewCount(product.getViewCount()+1);
            this.productRepository.save(product);
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
    }


    public Product findVerifiedProduct(long id){
        log.info("Verifying Product", id);

        Optional<Product> optionalProduct =
                productRepository.findById(id);
        Product findProduct =
                optionalProduct.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        return findProduct;

    }

    private void changeProductStatus(Product product){

        if(product.getProductCount() <= 5 && product.getProductCount() >= 1){
            product.setProductStatus(Product.ProductStatus.INSTOCK);
        }if(product.getProductCount() == 0)
            product.setProductStatus(Product.ProductStatus.SOLDOUT);
    }


}
