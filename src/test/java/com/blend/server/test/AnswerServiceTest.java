package com.blend.server.test;

import com.blend.server.answer.Answer;
import com.blend.server.answer.AnswerRepository;
import com.blend.server.answer.AnswerService;
import com.blend.server.product.Product;
import com.blend.server.review.Review;
import com.blend.server.review.ReviewRepository;
import com.blend.server.review.ReviewService;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerService;
import com.blend.server.user.User;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private SellerService sellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("답변 생성 테스트")
    @Test
    public void createAnswer(){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setSeller(seller);

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);


        Answer answer = new Answer();
        answer.setId(1L);
        answer.setReview(review);
        answer.setSeller(seller);
        answer.setContent("답변 내용");

        when(reviewService.findVerifiedReview(review.getId())).thenReturn(review);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(answerRepository.save(answer)).thenReturn(answer);

        Answer result = answerService.createAnswer(seller,answer,review.getId());

        assertNotNull(result);
        assertEquals(answer.getContent(), result.getContent());
    }

    @DisplayName("답변 수정 테스트")
    @Test
    public void updateAnswerTest(){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setSeller(seller);

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setReview(review);
        answer.setSeller(seller);
        answer.setContent("답변 내용");

        Answer updatedAnswer = new Answer();
        updatedAnswer.setId(1L);
        updatedAnswer.setReview(review);
        updatedAnswer.setSeller(seller);
        updatedAnswer.setContent("수정 내용");

        when(reviewService.findVerifiedReview(review.getId())).thenReturn(review);
        when(answerRepository.findByIdAndSeller(answer.getId(),seller)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(updatedAnswer);

        Answer result = answerService.updateAnswer(answer.getId(),seller,answer);

        assertNotNull(result);
        assertEquals(answer.getId(), result.getId());
        assertEquals(updatedAnswer.getContent(),result.getContent());
    }

    @DisplayName("답변 단일 조회 테스트")
    @Test
    public void findAnswerTest(){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setSeller(seller);

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);


        Answer answer = new Answer();
        answer.setId(1L);
        answer.setReview(review);
        answer.setSeller(seller);
        answer.setContent("답변 내용");

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        Answer result = answerService.findAnswer(answer.getId());

        assertNotNull(result);
        assertEquals(answer.getContent(), result.getContent());
    }

    @Test
    @DisplayName("판매자가 작성한 리뷰 답변 조회 테스트")
    public void findSellerAnswerTest(){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setSeller(seller);

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);


        List<Answer> sellerAnswers = Arrays.asList(
                createAnswer(1L, seller, review,"답변 1"),
                createAnswer(2L, seller, review,"답변 2"),
                createAnswer(3L, seller, review,"답변 3")
        );

        when(sellerService.findVerifiedSeller(seller.getId())).thenReturn(seller);
        when(answerRepository.findAllBySeller(
                seller,
                PageRequest.of(0, 10, Sort.by("id").descending())
        )).thenReturn(new PageImpl<>(sellerAnswers));

        Page<Answer> result = answerService.findSellerAnswers(10, 1, seller);

        assertNotNull(result);
        assertEquals(sellerAnswers.size(), result.getContent().size());
    }

    private Answer createAnswer(Long id,Seller seller, Review review, String content) {
        Answer answer = new Answer();
        answer.setId(id);
        answer.setSeller(seller);
        answer.setReview(review);
        answer.setContent(content);
        return answer;
    }

    @DisplayName("답변 삭제 테스트")
    @Test
    public void deleteAnswerTest(){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = new Product();
        product.setSeller(seller);

        Review review = new Review();
        review.setId(1L);
        review.setProduct(product);


        Answer answer = new Answer();
        answer.setId(1L);
        answer.setReview(review);
        answer.setSeller(seller);
        answer.setContent("답변 내용");

        when(answerRepository.findById(review.getId())).thenReturn(Optional.of(answer));
        when(answerRepository.findByIdAndSeller(review.getId(),seller)).thenReturn(Optional.of(answer));

        answerService.deleteAnswer(answer.getId(),seller);

        verify(answerRepository, times(1)).deleteById(answer.getId());
    }



}
