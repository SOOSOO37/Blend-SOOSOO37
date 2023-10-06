package com.blend.server.productImage;

import com.blend.server.Product.ProductService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductImageService {

    private final ProductService productService;

    private final ProductImageRepository productImageRepository;

    public ProductImage findProductImage(Long imageId) {
        Optional<ProductImage> optionalImage = productImageRepository.findById(imageId);
        ProductImage findImage = optionalImage.orElseThrow(() -> {
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        });
        productService.findProduct(findImage.getProduct().getId());

        return findImage;
    }
}
