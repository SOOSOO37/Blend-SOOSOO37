package com.blend.server.answer;

import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import com.blend.server.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Api(tags = "Answer API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/answers")
@RestController
public class AnswerController {

    private final AnswerService answerService;

    private final static String ANSWER_DEFAULT_URL = "/answers";
    private final AnswerMapper answerMapper;

    private final UserService userService;

    @ApiOperation(value = "답 생성 API")
    @PostMapping("/{review-id}")
    public ResponseEntity createAnswer (@PathVariable("review-id") long reviewId,
                                        @RequestBody AnswerPostDto answerPostDto,
                                        @AuthenticationPrincipal Seller seller) {
        log.info("-------Creating Answer-------");
        Answer answer = answerMapper.answerPostDtoToAnswer(answerPostDto);
        answer.setSeller(seller);
        Answer savedAnswer = answerService.createAnswer(seller,answer,reviewId);
        log.info("-------Created Answer: {} -------", answer.getId());
        URI location = UriCreator.createUri(ANSWER_DEFAULT_URL,savedAnswer.getId());

        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "답변 수정 API")
    @PatchMapping("/{id}")
    private ResponseEntity updateAnswer (@PathVariable("id")long id,
                                         @RequestBody AnswerPatchDto answerPatchDto,
                                         @AuthenticationPrincipal Seller seller){
        log.info("------- Updating Answer {} -------", id);
        answerPatchDto.setId(id);
        Answer updatedAnswer = answerService.updateAnswer(id,seller,answerMapper.answerPatchDtoToAnswer(answerPatchDto));
        AnswerResponseDto answerResponseDto = answerMapper.answerToAnswerResponseDto(updatedAnswer);
        log.info("------- Updated Answer {} -------", id);

        return new ResponseEntity(answerResponseDto, HttpStatus.OK);
    }
    @ApiOperation(value = "답변 조회 API")
    @GetMapping("/{id}")
    private ResponseEntity findAnswer(@PathVariable("id") long id){
        log.info("----- Inquiring Answer {} -----",id);
        Answer answer = answerService.findAnswer(id);
        log.info("----- Found Answer {} -----",id);
        return new ResponseEntity<>(answerMapper.answerToAnswerResponseDto(answer),HttpStatus.OK);
    }

    @ApiOperation(value = "판매자가 등록한 답변 조회 API")
    @GetMapping("/my-answers")
    public ResponseEntity findAnswerBySeller (@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @AuthenticationPrincipal Seller seller){
        log.info("Finding All Answer - Seller: {}, Page: {}, Size: {}", seller.getId(), page, size);
        Page<Answer> answerPage = answerService.findSellerAnswers(size,page,seller);
        List<Answer> answerList = answerPage.getContent();
        List<AnswerResponseDto> response = answerMapper.answersToAnswerResponseDto(answerList);
        log.info("Found Answers - Seller: {}, Page: {}, Size: {}, Total Orders: {}",
                seller.getId(), page, size, answerPage.getTotalElements());

        return new ResponseEntity<>(new MultiResponseDto<>(response,answerPage),HttpStatus.OK);
    }

    @ApiOperation(value = "답변 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAnswer(@PathVariable("id")long id,
                                       @AuthenticationPrincipal Seller seller){
        log.info("----- Deleting Answer {} -----",id);
        answerService.deleteAnswer(id,seller);
        log.info("----- Deleted Answer {} -----", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
