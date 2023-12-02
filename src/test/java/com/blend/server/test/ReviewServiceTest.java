package com.blend.server.test;

import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.review.Review;
import com.blend.server.review.ReviewRepository;
import com.blend.server.review.ReviewService;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.user.User;
import com.blend.server.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 모의 객체를 초기화하고 의존성 주입 (현재 테스트 클래스의 인스턴스를 초기화)
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 생성 테스트")
    public void createReviewTest() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        Review review = new Review();
        review.setTitle("테스트 리뷰");
        review.setContent("테스트 리뷰 내용");
        review.setScore(5);
        review.setUser(user);
        review.setProduct(product);

        List<ReviewImage> images = new ArrayList<>();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        when(reviewRepository.existsByUserAndProduct(any(), any())).thenReturn(false);

        when(reviewRepository.save(review)).thenReturn(review);

        Review savedReview = reviewService.createReview(review, images, user, 1L);

        assertNotNull(savedReview);
        assertEquals(user, savedReview.getUser());
        assertEquals(product, savedReview.getProduct());
    }

    @Test
    @DisplayName("리뷰 수정 테스트")
    public void updateReviewTest(){
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);
        review.setTitle("기존 제목");
        review.setContent("기존 내용");

        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setUser(user);
        updatedReview.setProduct(product);
        updatedReview.setTitle("수정된 제목");
        updatedReview.setContent("수정된 내용");

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.findByIdAndUser(review.getId(), user)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
        Review result = reviewService.updateReview(review.getId(),user,updatedReview);

        assertNotNull(result);
        assertEquals(review.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(updatedReview.getTitle(), result.getTitle());
        assertEquals(updatedReview.getContent(), result.getContent());

    }

    @Test
    @DisplayName("리뷰 단일 조회 테스트")
    public void findReviewTest(){
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);
        review.setTitle("기존 제목");
        review.setContent("기존 내용");
        review.setReviewStatus(Review.ReviewStatus.REVIEW_ACTIVE);

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        Review findReview = reviewService.findReview(review.getId());

        assertNotNull(findReview);
        assertEquals(review.getId(),findReview.getId());
        assertEquals(review.getTitle(),findReview.getTitle());
        assertEquals(user,findReview.getUser());
        assertEquals(product,findReview.getProduct());
    }


    @Test
    @DisplayName("회원이 작성한 리뷰 조회 테스트")
    public void findUserReviewsTest(){
        User user = new User();
        user.setId(1L);

        // 가상의 사용자 리뷰 목록 생성
        List<Review> userReviews = Arrays.asList(
                createReview(1L, user, "리뷰 1"),
                createReview(2L, user, "리뷰 2"),
                createReview(3L, user, "리뷰 3")
        );

        when(userService.findVerifiedUser(user.getId())).thenReturn(user);
        when(reviewRepository.findAllByUserAndReviewStatus(
                user,
                Review.ReviewStatus.REVIEW_ACTIVE,
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        )).thenReturn(new PageImpl<>(userReviews));

        // 테스트 대상 메서드 호출
        Page<Review> result = reviewService.findUserReviews(10, 1, user);

        // 결과 검증
        assertNotNull(result);
        assertEquals(userReviews.size(), result.getContent().size());
    }

    private Review createReview(Long id, User user, String title) {
        Review review = new Review();
        review.setId(id);
        review.setUser(user);
        review.setTitle(title);
        return review;
    }


    @Test
    @DisplayName("상품에 생성된 리뷰 조회 테스트")
    public void findProductReviewsTest(){
        // 상품 ID 생성
        Product product = new Product();
        product.setId(1L);

        // 가상의 상품 리뷰 목록 생성
        List<Review> reviews = Arrays.asList(
                createReview(1L, "리뷰 1",product),
                createReview(2L, "리뷰 2",product),
                createReview(3L, "리뷰 3",product)
        );

        when(reviewRepository.findAllByProductIdAndReviewStatus(
                product.getId(),
                Review.ReviewStatus.REVIEW_ACTIVE,
                PageRequest.of(0, 10, Sort.by("id").descending())
        )).thenReturn(new PageImpl<>(reviews));

        Page<Review> resultPage = reviewService.findProductReviews(10, 1, product.getId());

        assertNotNull(resultPage);
        assertEquals(reviews.size(), resultPage.getContent().size());
    }

    private Review createReview(Long id, String title,Product product) {
        Review review = new Review();
        review.setId(id);
        review.setTitle(title);
        review.setProduct(product);
        return review;
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    public void deleteReviewTest(){
        User user = new User();
        user.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setTitle("기존 제목");
        review.setContent("기존 내용");

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.findByIdAndUser(review.getId(), user)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        Review result = reviewService.deleteUserReview(review.getId(),user);

        assertNotNull(result);
        assertEquals(Review.ReviewStatus.REVIEW_DELETE, result.getReviewStatus());
    }
}
