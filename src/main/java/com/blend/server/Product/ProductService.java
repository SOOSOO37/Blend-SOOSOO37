package com.blend.server.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public Product updateProduct(Product product){
        Product findProduct = findVerifiedProduct(product.getId());

        Optional.ofNullable(product.getBrand())
                .ifPresent(brand -> findProduct.setBrand(brand));
        Optional.ofNullable(product.getProductName())
                .ifPresent(productName -> findProduct.setProductName(productName));
        Optional.ofNullable(product.getCategory())
                .ifPresent(category -> findProduct.setCategory(category));
        Optional.ofNullable(product.getRanking())
                .ifPresent(ranking -> findProduct.setRanking(ranking));
        Optional.ofNullable(product.getPrice())
                .ifPresent(price -> findProduct.setPrice(price));
        Optional.ofNullable(product.getSalePrice())
                .ifPresent(salePrice -> findProduct.setSalePrice(salePrice));
        Optional.ofNullable(product.getImage())
                .ifPresent(image -> findProduct.setImage(image));
        Optional.ofNullable(product.getInfo())
                .ifPresent(info -> findProduct.setInfo(info));
        Optional.ofNullable(product.getSizeInfo())
                .ifPresent(sizeInfo -> findProduct.setSizeInfo(sizeInfo));
        Optional.ofNullable(product.getProductCount())
                .ifPresent(productCount -> findProduct.setProductCount(productCount));


        return productRepository.save(findProduct);
    }
    //상태변경
    public Product updateStatus(long id){
        Product findProduct = findVerifiedProduct(id);

        if(findProduct.getProductCount() >= 5 && findProduct.getProductCount() >= 1){
            findProduct.setProductStatus(Product.ProductStatus.INSTOCK);
        }if(findProduct.getProductCount() == 0)
        findProduct.setProductStatus(Product.ProductStatus.SOLDOUT);

        return productRepository.save(findProduct);

    }
    //상세조회(id 조회)
    public Product findProduct(long id){
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            product.setViewCount(product.getViewCount()+1);
            this.productRepository.save(product);
            return product;
        }else {
            throw new RuntimeException("Product Not Found");
        }

    }

    //실시간 랭킹 조회(조회순)
    public Page<Product> findProductRanks(int page, int size){
        return productRepository.findAll(PageRequest.of(page,size, Sort.by("viewCount").descending()));

    }

    //카테고리 조회
    public Page<Product> findCategory(int page, int size, String category){

        return productRepository.findByCategory(category, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    //판매중인 상품 조회
    public Page<Product> findSaleProduct (int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return productRepository.findByProductStatus(Product.ProductStatus.SALE, pageable);

    }

    public void deleteProduct(long id){
        Product findProduct = findVerifiedProduct(id);

        productRepository.deleteById(id);
    }


    public Product findVerifiedProduct(long id){
        Optional<Product> optionalProduct =
                productRepository.findById(id);
        Product findProduct =
                optionalProduct.orElseThrow(() ->
                        new RuntimeException());
        return findProduct;

    }




}
