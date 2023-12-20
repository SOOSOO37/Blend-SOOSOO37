package com.blend.server.productImage;

import com.blend.server.product.ProductService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductImageService {

    private final ProductService productService;

    private final ProductImageRepository productImageRepository;

    public ProductImage findProductImage(Long imageId) {
        log.info("---Finding Product Image---");
        Optional<ProductImage> optionalImage = productImageRepository.findById(imageId);
        ProductImage findImage = optionalImage.orElseThrow(() -> {
            log.warn("Product image {} not found.", imageId);
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        });
        productService.findProduct(findImage.getProduct().getId());
        log.info("Found Product Image: {}", imageId);
        return findImage;
    }
}
