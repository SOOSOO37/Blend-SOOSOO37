package com.blend.server.Product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    //상품 등록
    @PostMapping
    public Product createProducts(@RequestBody Product product){
        return productService.createProduct(product);
    }

    //id 조회
    @GetMapping("/{id}")
    public Product findById(@PathVariable long id){
        return productService.findById(id);
    }

    //전체 조회
    @GetMapping("/ranking")
    public List<Product> findAll(){
        return productService.findAll();
    }


}
