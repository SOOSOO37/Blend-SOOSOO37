package com.blend.server.reviewImage;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.productImage.ProductImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;

    public ReviewImage findReviewImage(Long imageId) {
        log.info("---Finding Review Image---");
        Optional<ReviewImage> optionalImage = reviewImageRepository.findById(imageId);
        ReviewImage findImage = optionalImage.orElseThrow(() -> {
            log.warn("Review image {} not found.", imageId);
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        });
        log.info("Found Review Image: {}", imageId);
        return findImage;
    }
}
