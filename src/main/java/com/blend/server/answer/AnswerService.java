package com.blend.server.answer;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.review.Review;
import com.blend.server.review.ReviewRepository;
import com.blend.server.review.ReviewService;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerService;
import com.blend.server.user.User;
import com.blend.server.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    private final SellerService sellerService;

    public Answer createAnswer (Seller seller, Answer answer, long reviewId) {
        log.info("---Creating Answer ---");
        verifySeller(reviewId,seller.getId());
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
        answer.setReview(review);
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Created  Answer : {}", savedAnswer.getId());
        return savedAnswer;

    }

    public Answer updateAnswer(long id, Seller seller, Answer answer){
        log.info("--- Updating Answer for Seller ID: {}, Product ID: {} ---", seller, id);
        verifySeller(id,seller.getId());
        Answer findAnswer = findAnswerBySeller(id,seller);
        log.info("Updating Answer: {}", answer);
        BeanUtils.copyProperties(answer,findAnswer,"createdAt","review","seller");
        log.info("Updated Answer: {}", findAnswer);

        return answerRepository.save(findAnswer);
    }

    //단일 답변 조회
    public Answer findAnswer(long id){
        log.info("---Inquiring Answer{}---",id);
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        Answer answer = optionalAnswer.orElseThrow(() ->{
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        });
        log.info("Found Answer - AnswerId: {}", id);
        return answer;
    }
    //판매자가 한 답변 리스트
    public Page<Answer> findSellerAnswers(int size, int page, Seller seller){
        log.info("Finding seller answers - SellerId: {}, Page: {}, Size: {}", seller.getId(), page, size);
        Seller findSeller = sellerService.findVerifiedSeller(seller.getId());
        Page<Answer> sellerAnswers = answerRepository.findAllBySeller(findSeller,
                PageRequest.of(page - 1, size, Sort.by("id").descending()));

        log.info("Found Seller answers - SellerId: {}, Page: {}, Size: {}, Total Answers: {}",
                seller.getId(), page, size, sellerAnswers.getTotalElements());

        return sellerAnswers;
    }
    //답변 삭제
    public void deleteAnswer (long id , Seller seller){
        log.info("Deleting answer - AnswerId: {}, SellerId: {}", id, seller.getId());
        verifyAnswerSeller(id, seller.getId());
        Answer findAnswer = findAnswerBySeller(id, seller);
        answerRepository.deleteById(findAnswer.getId());
        log.info("Deleted Answer - AnswerId: {}, SellerId: {}", id, seller.getId());
    }

    private Answer findAnswerBySeller(long id, Seller seller){
        log.info("Finding answer by seller - AnswerId: {}, SellerId: {}", id, seller.getId());
        Answer answer = answerRepository.findByIdAndSeller(id, seller)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ADMIN_NOT_FOUND));
        log.info("Found Answer - AnswerId: {}, SellerId: {}", id, seller.getId());

        return answer;
    }

    public void verifySeller(long reviewId, long sellerId){
        log.info("---Verifying Seller---");
        Review review = reviewService.findVerifiedReview(reviewId);
        long dbSellerId = review.getProduct().getSeller().getId();

        if(sellerId != dbSellerId){
            log.warn("Seller{} is not authorized {}", sellerId, reviewId);
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
        log.info("Verified Seller : {}, Review ID: {}", sellerId, reviewId);
    }

    public void verifyAnswerSeller(long answerId, long sellerId){
        log.info("---Verifying Answer Seller---");
        Answer answer = findAnswer(answerId);
        long dbSellerId = answer.getSeller().getId();

        if(sellerId != dbSellerId){
            log.warn("Seller{} is not authorized {}", sellerId, answerId);
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
        log.info("Verified Seller : {}, Answer ID: {}", sellerId, answerId);
    }
}
