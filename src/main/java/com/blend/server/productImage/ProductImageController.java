package com.blend.server.productImage;

import com.blend.server.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ProductImageController {

    private final ProductImageService productImageService;

    private final ProductService productService;

    @GetMapping("/{productImages-id}")
    public ResponseEntity<byte[]> getProductImages(@PathVariable("productImages-id") Long imageId){
        ProductImage Image = productImageService.findProductImage(imageId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(
                Image.getType()));

        return new ResponseEntity<byte[]>(Image.getImage(), headers, HttpStatus.OK);
    }
}
