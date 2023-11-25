package com.blend.server.answer;

import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import com.blend.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/answers")
@RestController
public class AnswerController {

    private final AnswerService answerService;

    private final static String ANSWER_DEFAULT_URL = "/answers";
    private final AnswerMapper answerMapper;

    private final UserService userService;

    @PostMapping("/{review-id}")
    public ResponseEntity createAnswer (@PathVariable("review-id") long reviewId,
                                        @RequestBody AnswerPostDto answerPostDto,
                                        @AuthenticationPrincipal Seller seller) {

        Answer answer = answerMapper.answerPostDtoToAnswer(answerPostDto);
        answer.setSeller(seller);
        Answer savedAnswer = answerService.createAnswer(seller,answer,reviewId);
        URI location = UriCreator.createUri(ANSWER_DEFAULT_URL,savedAnswer.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    private ResponseEntity updateAnswer (@PathVariable("id")long id,
                                         @RequestBody AnswerPatchDto answerPatchDto,
                                         @AuthenticationPrincipal Seller seller){
        answerPatchDto.setId(id);
        Answer updatedAnswer = answerService.updateAnswer(id,seller,answerMapper.answerPatchDtoToAnswer(answerPatchDto));
        AnswerResponseDto answerResponseDto = answerMapper.answerToAnswerResponseDto(updatedAnswer);

        return new ResponseEntity(answerResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    private ResponseEntity findAnswer(@PathVariable("id") long id){
        Answer answer = answerService.findAnswer(id);

        return new ResponseEntity<>(answerMapper.answerToAnswerResponseDto(answer),HttpStatus.OK);
    }

    @GetMapping("/my-answers")
    public ResponseEntity findAnswerBySeller (@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @AuthenticationPrincipal Seller seller){
        Page<Answer> answerPage = answerService.findSellerAnswers(size,page,seller);
        List<Answer> answerList = answerPage.getContent();
        List<AnswerResponseDto> response = answerMapper.answersToAnswerResponseDto(answerList);

        return new ResponseEntity<>(new MultiResponseDto<>(response,answerPage),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAnswer(@PathVariable("id")long id,
                                       @AuthenticationPrincipal Seller seller){
        answerService.deleteAnswer(id,seller);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
