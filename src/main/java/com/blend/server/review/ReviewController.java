package com.blend.server.review;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.order.Order;
import com.blend.server.product.Product;
import com.blend.server.productImage.ProductImage;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    private final static String REVIEW_DEFAULT_URL = "/reviews";
    private final ReviewMapper mapper;

    @Value("${config.domain}")
    private String domain;

    @PostMapping
    private ResponseEntity createReview(@RequestPart(name = "post")ReviewPostDto postDto,
                                        @RequestPart(required = false, name = "imageFiles")List<MultipartFile> multipartFiles,
                                        @RequestParam Long productId,
                                        @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 인증되지 않았습니다.");
        }

        try {
            Review review = mapper.reviewPostDtoToReview(postDto);
            List<ReviewImage> imageList = mapper.multipartFilesToImages(multipartFiles);

            review.setUser(user);

            // 리뷰 생성 및 저장
            Review savedReview = reviewService.createReview(review, imageList, user,productId);

            // 생성된 리뷰의 URI를 구성
            URI location = UriCreator.createUri(REVIEW_DEFAULT_URL,review.getId());

            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateReview(@PathVariable("id")long id,
                                        @RequestBody ReviewPatchDto reviewPatchDto,
                                        @AuthenticationPrincipal User user) {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        reviewPatchDto.setId(id);
        Review updatedReview = reviewService.updateReview(id,user,mapper.reviewPatchDtoToReview(reviewPatchDto));
        ReviewDetailResponseDto reviewResponseDto = mapper.reviewToReviewResponseDto(updatedReview,domain);

        return new ResponseEntity<>(reviewResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findReview(@PathVariable("id") long id){
        Review review = reviewService.findReview(id);

        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review,domain),HttpStatus.OK);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity findReviewsByUser (@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @AuthenticationPrincipal User user){

        Page<Review> reviewPage = reviewService.findUserReviews(size, page, user);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewDetailResponseDto> response = mapper.reviewsToReviewResponseDto(reviewList,domain);

        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @GetMapping("/product-reviews/{product-id}")
    public ResponseEntity findReviewsByProducts (@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @PathVariable("product-id") long productId){

        Page<Review> reviewPage = reviewService.findProductReviews(size, page,productId);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewDetailResponseDto> response = mapper.reviewsToReviewResponseDto(reviewList,domain);

        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @PatchMapping("/removed/{id}")
    public ResponseEntity deleteReviews (@PathVariable long id,
                                         @AuthenticationPrincipal User user){

        Review review = reviewService.deleteReview(id,user);
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review,domain),HttpStatus.OK);
    }

}
