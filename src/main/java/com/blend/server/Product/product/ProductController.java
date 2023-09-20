package com.blend.server.Product.product;

import com.blend.server.Product.category.Category;
import com.blend.server.Product.category.CategoryService;
import com.blend.server.Product.global.dto.MultiResponseDto;
import com.blend.server.Product.utils.UriCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final static String PRODUCT_DEFAULT_URL = "/products";

    private final ProductService productService;

    private final CategoryService categoryService;

    private final ProductMapper mapper;

    public ProductController(ProductService productService, CategoryService categoryService, ProductMapper mapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postProduct(@RequestBody ProductPostDto productPostDto,
                                      @RequestParam Long categoryId) {
        logger.info("-------Creating Product-------");


        Product product = productService.createProduct(mapper.productPostDtoToProduct(productPostDto), categoryId);

        URI location = UriCreator.createUri(PRODUCT_DEFAULT_URL, product.getId());

        logger.info("-------Product ID: {} -------", product.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") long id,
                                        @RequestBody ProductPatchDto productPatchDto) {
        logger.info("------- Updating Product -------", id);
        productPatchDto.setId(id);
        Product updateProduct = productService.updateProduct(id,mapper.productPatchDtoToProduct(productPatchDto), productPatchDto.getCategoryId());

        logger.info("------- Updated Product -------", id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(updateProduct), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findProduct(@PathVariable("id") long id){
        logger.info("----- Inquiring Product -----",id);

        Product product = productService.findProduct(id);

        if(product == null){
            logger.warn("------ Product not found ------",id);
            return ResponseEntity.notFound().build();
        }

        logger.info("----- Found Product -----",id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(product),HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity findRanking(@RequestParam int page,
                                     @RequestParam int size){

        logger.info("----- Inquiring Ranking -----");

        Page<Product> products = productService.findProductRanks(page -1, size);
        List<Product> productList = products.getContent();

        logger.info("----- Ranking -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),products),HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity findCategoryContent(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam String name){

        logger.info("----- Inquiring Category -----");
        Category category = categoryService.findCategory(name);
        Page<Product> productPage = productService.findCategory(page -1, size, name);
        List<Product> productList = productPage.getContent();

        logger.info("----- Category -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),productPage),HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id")long id){
        logger.info("----- deleting Product -----",id);

        productService.deleteProduct(id);

        logger.info("----- deleted Product -----",id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/all")
    public ResponseEntity findAllProducts(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size){

        logger.info("----- Search for Products on sale -----");

        Page<Product> productPage = productService.findSaleProduct(page -1 ,size);
        List<Product> productList = productPage.getContent();

        logger.info("----- On Sale List -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),productPage),HttpStatus.OK);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity updateStatus(@PathVariable("id")long id){
        logger.info("----- Updating Status -----",id);

        Product product = productService.updateStatus(id);

        logger.info("----- Updated Status -----",id);
        return new ResponseEntity<>(product,HttpStatus.OK);

    }


}
