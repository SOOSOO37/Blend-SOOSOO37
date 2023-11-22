package com.blend.server.review;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.product.Product;
import com.blend.server.reviewImage.ReviewImage;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    default Review reviewPostDtoToReview (ReviewPostDto reviewPostDto){
        Product product = new Product();
        Review review = new Review();

        review.setProduct(product);
        BeanUtils.copyProperties(reviewPostDto, review);

        return review;
    }

    Review reviewPatchDtoToReview(ReviewPatchDto reviewPatchDto);

    default ReviewDetailResponseDto reviewToReviewResponseDto (Review review, String domain){
        if (review == null){
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        }

        ReviewDetailResponseDto response = new ReviewDetailResponseDto();
        List<String> links = review.getImages().stream()
                .map(reviewImage -> {
                    String link = domain+"/review-images/"+reviewImage.getId();
                    return link;
                })
                .collect(Collectors.toList());

        response.setId(review.getId());
        response.setTitle(review.getTitle());
        response.setContent(review.getContent());
        response.setScore(review.getScore());
        response.setReviewStatus(review.getReviewStatus());
        response.setReviewImageUrls(links);
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    default List<ReviewDetailResponseDto> reviewsToReviewResponseDto(List<Review> reviews , String domain) {
        if (reviews == null) {
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        }
        List<ReviewDetailResponseDto> response = new ArrayList<ReviewDetailResponseDto>(reviews.size());
        for (Review review : reviews) {
            response.add(reviewToReviewResponseDto(review, domain));
        }
        return response;
    }

    default List<ReviewImage> multipartFilesToImages(List<MultipartFile> multipartFiles) throws IOException{
        if (multipartFiles == null){
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        }

        List<ReviewImage> images = multipartFiles.stream()
                .map(multipartFile -> {
                    ReviewImage reviewImage = new ReviewImage();
                    try {
                        reviewImage.setImage(multipartFile.getBytes());
                        reviewImage.setType(multipartFile.getContentType());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return reviewImage;
                })
                .collect(Collectors.toList());
        return images;
    }
}
