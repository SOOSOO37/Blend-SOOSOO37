package com.blend.server.Product;

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

    private final ProductMapper mapper;

    public ProductController(ProductService productService, ProductMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    //상품 등록
    @PostMapping
    public ResponseEntity postProduct(@RequestBody ProductPostDto productPostDto){
        logger.info("-------Creating Product-------");


        Product product = productService.createProduct(mapper.productPostDtoToProduct(productPostDto));

        URI location = UriCreator.createUri(PRODUCT_DEFAULT_URL, product.getId());

        logger.info("-------Product ID: {} -------", product.getId());

        return ResponseEntity.created(location).build();
    }

    //상품 수정
    @PatchMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") long id,
                                        @RequestBody ProductPatchDto productPatchDto){
        logger.info("------- Updating Product -------",id);

        productPatchDto.setId(id);
        Product product = productService.updateProduct(mapper.productPatchDtoToProduct(productPatchDto));

        logger.info("------- Updated Product -------",id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(product), HttpStatus.OK);
    }

    //상품 상세조회(id 조회)
    @GetMapping("/{id}")
    public ResponseEntity getProduct(@PathVariable("id") long id){
        logger.info("----- Inquiring Product -----",id);

        Product product = productService.findProduct(id);

        if(product == null){
            logger.warn("------ Product not found ------",id);
            return ResponseEntity.notFound().build();
        }

        logger.info("----- Found Product -----",id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(product),HttpStatus.OK);
    }

    //실시간랭킹 조회(view수)
    @GetMapping("/ranking")
    public ResponseEntity getRanking(@RequestParam int page,
                                     @RequestParam int size){

        logger.info("----- Inquiring Ranking -----");

        Page<Product> products = productService.findProductRanks(page -1, size);
        List<Product> productList = products.getContent();

        logger.info("----- Ranking -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),products),HttpStatus.OK);
    }

    //카테고리별 상품 조회
    @GetMapping("/category")
    public ResponseEntity getCategoryContent(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false, defaultValue = "") String category){

        logger.info("----- Inquiring Category -----");

        Page<Product> productPage = productService.findCategory(page -1, size, category);
        List<Product> productList = productPage.getContent();

        logger.info("----- Category -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),productPage),HttpStatus.OK);

    }

    //상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id")long id){
        logger.info("----- deleting Product -----",id);

        productService.deleteProduct(id);

        logger.info("----- deleted Product -----",id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    //판매중인 상품 조회
    @GetMapping("/all")
    public ResponseEntity getAllProducts(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size){

        logger.info("----- Search for Products on sale -----");

        Page<Product> productPage = productService.findSaleProduct(page -1 ,size);
        List<Product> productList = productPage.getContent();

        logger.info("----- On Sale List -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),productPage),HttpStatus.OK);

    }

    //상태 업데이트(판매중/품절/재고5개 미만)
    @PatchMapping("/{id}/status")
    public ResponseEntity updateStatus(@PathVariable("id")long id){
        logger.info("----- Updating Status -----",id);

        Product product = productService.updateStatus(id);

        logger.info("----- Updated Status -----",id);
        return new ResponseEntity<>(HttpStatus.OK);

    }


}
