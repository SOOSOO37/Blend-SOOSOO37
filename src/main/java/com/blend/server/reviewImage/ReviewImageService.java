package com.blend.server.reviewImage;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.productImage.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;

    public ReviewImage findReviewImage(Long imageId) {
        Optional<ReviewImage> optionalImage = reviewImageRepository.findById(imageId);
        ReviewImage findImage = optionalImage.orElseThrow(() -> {
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        });

        return findImage;
    }
}
