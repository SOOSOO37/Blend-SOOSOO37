package com.blend.server.Product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public Product findById(long id){
        return productRepository.findById(id);
    }

    public List<Product> findAll (){
        return productRepository.findAll();
    }
}
