package com.blend.server.review;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.order.Order;
import com.blend.server.product.Product;
import com.blend.server.productImage.ProductImage;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Api(tags = "Review API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    private final static String REVIEW_DEFAULT_URL = "/reviews";
    private final ReviewMapper mapper;

    @Value("${config.domain}")
    private String domain;

    @ApiOperation(value = "리뷰 등록 API")
    @PostMapping
    private ResponseEntity createReview(@RequestPart(name = "post")ReviewPostDto postDto,
                                        @RequestPart(required = false, name = "imageFiles")List<MultipartFile> multipartFiles,
                                        @RequestParam Long productId,
                                        @AuthenticationPrincipal User user) {
        log.info("---Creating Review---");
        if (user == null) {
            log.warn("User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 인증되지 않았습니다.");
        }

        try {
            Review review = mapper.reviewPostDtoToReview(postDto);
            List<ReviewImage> imageList = mapper.multipartFilesToImages(multipartFiles);

            review.setUser(user);

            Review savedReview = reviewService.createReview(review, imageList, user,productId);

            URI location = UriCreator.createUri(REVIEW_DEFAULT_URL,review.getId());
            log.info("Created Review: {}", savedReview.getId());
            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            log.error("Error creating review: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "리뷰 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity updateReview(@PathVariable("id")long id,
                                        @RequestBody ReviewPatchDto reviewPatchDto,
                                        @AuthenticationPrincipal User user) {
        log.info("---Updating Review---");
        if(user == null){
            log.warn("User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        reviewPatchDto.setId(id);
        Review updatedReview = reviewService.updateReview(id,user,mapper.reviewPatchDtoToReview(reviewPatchDto));
        ReviewDetailResponseDto reviewResponseDto = mapper.reviewToReviewResponseDto(updatedReview,domain);
        log.info("Updated Review : {}", updatedReview.getId());
        return new ResponseEntity<>(reviewResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단일 리뷰 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity findReview(@PathVariable("id") long id){
        log.info("---Finding Review---");
        Review review = reviewService.findReview(id);
        log.info("Found Review : {}", review.getId());
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review,domain),HttpStatus.OK);
    }

    @ApiOperation(value = "사용자가 작성한 리뷰 목록 조회 API")
    @GetMapping("/my-reviews")
    public ResponseEntity findReviewsByUser (@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @AuthenticationPrincipal User user){

        log.info("---Finding Reviews by User---");
        Page<Review> reviewPage = reviewService.findUserReviews(size, page, user);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewDetailResponseDto> response = mapper.reviewsToReviewResponseDto(reviewList,domain);
        log.info("Found Reviews : {}", user.getId());
        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @ApiOperation(value = "상품에 작성된 리뷰 목록 조회 API")
    @GetMapping("/product-reviews/{product-id}")
    public ResponseEntity findReviewsByProducts (@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @PathVariable("product-id") long productId){

        log.info("---Finding Reviews by Product---");
        Page<Review> reviewPage = reviewService.findProductReviews(size, page,productId);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewDetailResponseDto> response = mapper.reviewsToReviewResponseDto(reviewList,domain);

        log.info("Found Reviews: {}", productId);
        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @ApiOperation(value = "리뷰 삭제 API")
    @PatchMapping("/removed/{id}")
    public ResponseEntity deleteReviews (@PathVariable long id,
                                         @AuthenticationPrincipal User user){
        log.info("---Deleting Review---");
        Review review = reviewService.deleteUserReview(id,user);
        log.info("Deleted Review : {}", id);
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review,domain),HttpStatus.OK);
    }

}
