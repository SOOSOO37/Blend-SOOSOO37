package com.blend.server.review;

import com.blend.server.category.Category;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.product.ProductService;
import com.blend.server.productImage.ProductImage;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.reviewImage.ReviewImageRepository;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import com.blend.server.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public Review createReview(Review review, List<ReviewImage> images, User user, long productId) {

        log.info("---Creating Review---");
        if (user != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));

            if (reviewRepository.existsByUserAndProduct(review.getUser(), product)) {
                log.warn("Review already exists {} and product ID {}", user.getId(), productId);
                throw new RuntimeException("이미 리뷰가 존재합니다.");
            }
            if (images != null) {
                List<ReviewImage> imageList = images.stream()
                        .map(image -> {
                            review.addImage(image);
                            return image;
                        })
                        .collect(Collectors.toList());
            }
            review.setProduct(product);
            Review savedReview = reviewRepository.save(review);
            log.info("Created Review : {}", savedReview.getId());
            return savedReview;
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public Review updateReview (long id, User user,Review review){
        log.info("---Updating Review---");
        verifyUser(id,user.getId());
        Review findReview = findReviewByUser(id,user);

        BeanUtils.copyProperties(review, findReview,"user","product","images","createdAt");
        Review updatedReview = reviewRepository.save(findReview);
        log.info("Updated Review : {}", updatedReview.getId());

        return updatedReview;
    }

    public Review findReview(long id){
        log.info("---Finding Review---");
        Optional<Review> optionalReview = reviewRepository.findById(id);
        Review findReview = optionalReview.orElseThrow(() ->{
            log.warn("Review not found {}", id);
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        });
        verifiedActiveReview(findReview);
        log.info("Found Review : {}", id);
        return findReview;
    }
    //회원이 작성한 리뷰
    public Page<Review> findUserReviews(int size, int page, User user){
        log.info("---Finding Reviews by User---");
        User findUser = userService.findVerifiedUser(user.getId());
        return reviewRepository.findAllByUserAndReviewStatus(findUser,
                Review.ReviewStatus.REVIEW_ACTIVE, PageRequest.of(page-1,size, Sort.by("createdAt").descending()));

    }
    //상품 리뷰
    public Page<Review> findProductReviews(int size, int page, long productId){
        log.info("---Finding Reviews by Product---");
        return reviewRepository.findAllByProductIdAndReviewStatus(productId, Review.ReviewStatus.REVIEW_ACTIVE,
                PageRequest.of(page-1, size, Sort.by("id").descending()));
    }



    public Review deleteUserReview(long id, User user){
        log.info("---Deleting User Review---");
        Review findReview = findReviewByUser(id,user);
        verifyUser(id, user.getId());
        findReview.setReviewStatus(Review.ReviewStatus.REVIEW_DELETE);
        Review deletedReview = reviewRepository.save(findReview);
        log.info("Deleted Review : {}", deletedReview.getId());

        return deletedReview;
    }

    private void verifiedActiveReview(Review review){
        if(review.getReviewStatus().getNumber() == 2) {
            log.warn("Review {} is removed.", review.getId());
            throw new BusinessLogicException(ExceptionCode.REVIEW_REMOVED);
        }
    }

    public Review findVerifiedReview(long id){
        log.info("---Finding Verified Review---");
        Optional<Review> optionalReview = reviewRepository.findById(id);
        Review findReview =
                optionalReview.orElseThrow(() ->
                    new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
        log.info("Found Review : {}", id);
        return findReview;
    }

    private Review findReviewByUser(long reviewId, User user){
        log.info("---Finding Review by User---");
        return reviewRepository.findByIdAndUser(reviewId,user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
    }

    public void verifyUser(long reviewId, long userId){
        log.info("---Verifying User---");
        Review findReview = findVerifiedReview(reviewId);
        long dbUserId = findReview.getUser().getId();

        if(userId != dbUserId){
            log.warn("User{} is not authorized to access review {}", userId, reviewId);
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        log.info("Verified User : {}, Review ID: {}", userId, reviewId);
    }

}
