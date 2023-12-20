package com.blend.server.productImage;

import com.blend.server.product.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ProductImage API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ProductImageController {

    private final ProductImageService productImageService;

    private final ProductService productService;

    @ApiOperation(value = "상품 이미지 조회 API")
    @GetMapping("/{productImages-id}")
    public ResponseEntity<byte[]> getProductImages(@PathVariable("productImages-id") Long imageId){
        log.info("---Finding Review Image---");
        ProductImage Image = productImageService.findProductImage(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Image.getType()));
        log.info("Found Product Image : {}", imageId);
        return new ResponseEntity<byte[]>(Image.getImage(), headers, HttpStatus.OK);
    }
}
