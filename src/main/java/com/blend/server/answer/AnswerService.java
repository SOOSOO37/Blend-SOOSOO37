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
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    private final SellerService sellerService;

    public Answer createAnswer (Seller seller, Answer answer, long reviewId) {

        verifySeller(reviewId,seller.getId());
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
        answer.setReview(review);
        Answer savedAnswer = answerRepository.save(answer);

        return savedAnswer;

    }

    public Answer updateAnswer(long id, Seller seller, Answer answer){
        verifySeller(id,seller.getId());
        Answer findAnswer = findAnswerBySeller(id,seller);

        BeanUtils.copyProperties(answer,findAnswer,"createdAt","review","seller");

        return answerRepository.save(findAnswer);
    }

    //단일 답변 조회
    public Answer findAnswer(long id){
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        Answer answer = optionalAnswer.orElseThrow(() ->{
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        });
        return answer;
    }
    //판매자가 한 답변 리스트
    public Page<Answer> findSellerAnswers(int size, int page, Seller seller){
        Seller findSeller = sellerService.findVerifiedSeller(seller.getId());
        return answerRepository.findAllBySeller(findSeller, PageRequest.of(page-1,size, Sort.by("id").descending()));
    }
    //답변 삭제
    public void deleteAnswer (long id , Seller seller){
        verifyAnswerSeller(id, seller.getId());
        Answer findAnswer = findAnswerBySeller(id, seller);

        answerRepository.deleteById(findAnswer.getId());
    }

    private Answer findAnswerBySeller(long id, Seller seller){
        return answerRepository.findByIdAndSeller(id,seller)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.ADMIN_NOT_FOUND));
    }

    public void verifySeller(long reviewId, long sellerId){

        Review review = reviewService.findVerifiedReview(reviewId);
        long dbSellerId = review.getProduct().getSeller().getId();

        if(sellerId != dbSellerId){
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
    }



    public void verifyAnswerSeller(long answerId, long sellerId){
        Answer answer = findAnswer(answerId);
        long dbSellerId = answer.getSeller().getId();

        if(sellerId != dbSellerId){
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
    }

}
