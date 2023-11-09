package com.blend.server.Product;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryService;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.productImage.ProductImage;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    @Value("${config.domain}")
    private String domain;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final static String PRODUCT_DEFAULT_URL = "/products";

    private final ProductService productService;

    private final CategoryService categoryService;

    private final SellerService sellerService;

    private final ProductMapper mapper;


    @PostMapping
    public ResponseEntity postProduct(@RequestPart(name = "post") ProductCreateDto productCreateDto,
                                      @RequestPart(required = false, name = "imageFiles") List<MultipartFile> imageFiles,
                                      @RequestParam Long categoryId,
                                      @AuthenticationPrincipal Seller seller) {

        logger.info("-------Creating Product-------");

        if (seller == null) {
            // 판매자가 로그인되어 있지 않으면 401 Unauthorized 상태 코드와 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Seller not authenticated.");
        }

        try {
            // ProductCreateDto를 Product 엔티티로 매핑
            Product product = mapper.productPostDtoToProduct(productCreateDto);

            List<ProductImage> productImageList = mapper.multipartFilesToProductImages(imageFiles);

            product.setSeller(seller);

            Product createProduct = productService.createProduct(product, categoryId, productImageList,seller);
            URI location = UriCreator.createUri(PRODUCT_DEFAULT_URL, product.getId());

            logger.info("-------Product ID: {} -------", product.getId());

            return ResponseEntity.created(location).build();
        } catch (BusinessLogicException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @ApiOperation(value = "상품 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") long id,
                                        @RequestBody ProductUpdateDto productUpdateDto) {
        logger.info("------- Updating Product -------", id);
        productUpdateDto.setId(id);
        Product updateProduct = productService.updateProduct(id,mapper.productPatchDtoToProduct(productUpdateDto), productUpdateDto.getCategoryId());

        logger.info("------- Updated Product -------", id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(updateProduct,domain), HttpStatus.OK);
    }

    @ApiOperation(value = "상품 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity findProduct(@PathVariable("id") long id){
        logger.info("----- Inquiring Product -----",id);

        Product product = productService.findProduct(id);

        if(product == null){
            logger.warn("------ Product not found ------",id);
            return ResponseEntity.notFound().build();
        }

        logger.info("----- Found Product -----",id);

        return new ResponseEntity<>(mapper.productToProductDetailResponseDto(product,domain),HttpStatus.OK);
    }

    @ApiOperation(value = "상품 실시간 랭킹(조회수 순위순) 조회 API")
    @GetMapping("/ranking")
    public ResponseEntity findRanking(@RequestParam int page,
                                     @RequestParam int size){

        logger.info("----- Inquiring Ranking -----");

        Page<Product> products = productService.findProductRanks(page -1, size);
        List<Product> productList = products.getContent();

        logger.info("----- Ranking -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),products),HttpStatus.OK);
    }

    @ApiOperation(value = "상품 카테고리 조회 API")
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

    @ApiOperation(value = "상품 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id")long id){
        logger.info("----- deleting Product -----",id);

        productService.deleteProduct(id);

        logger.info("----- deleted Product -----",id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @ApiOperation(value = "전체 상품 조회 API")
    @GetMapping
    public ResponseEntity findAllProducts(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size){

        logger.info("----- Search for Products on sale -----");

        Page<Product> productPage = productService.findSaleProduct(page -1 ,size);
        List<Product> productList = productPage.getContent();

        logger.info("----- On Sale List -----");

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.productsToProductResponseDtos(productList),productPage),HttpStatus.OK);

    }

    @ApiOperation(value = "상품 상태 변경 API")
    @PatchMapping("/{id}/status")
    public ResponseEntity updateStatus(@PathVariable("id")long id){
        logger.info("----- Updating Status -----",id);

        Product product = productService.updateStatus(id);

        logger.info("----- Updated Status -----",id);
        return new ResponseEntity<>(product,HttpStatus.OK);

    }
}
