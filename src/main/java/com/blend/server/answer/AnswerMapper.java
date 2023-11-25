package com.blend.server.answer;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.review.Review;
import com.blend.server.user.User;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    default Answer answerPostDtoToAnswer (AnswerPostDto answerPostDto){
        Review review = new Review();
        Answer answer = new Answer();

        answer.setReview(review);
        BeanUtils.copyProperties(answerPostDto, answer);

        return answer;
    }

    Answer answerPatchDtoToAnswer(AnswerPatchDto answerPatchDto);

    AnswerResponseDto answerToAnswerResponseDto (Answer answer);

    default List<AnswerResponseDto> answersToAnswerResponseDto(List<Answer> answers){
        if (answers == null) {
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        }
        List<AnswerResponseDto> response = new ArrayList<AnswerResponseDto>(answers.size());
        for(Answer answer : answers){
            response.add(answerToAnswerResponseDto(answer));
        }
        return response;
    }
}
