package com.blend.server.reviewImage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ReviewImage API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/review-images")
@RestController
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @ApiOperation(value = "리뷰 이미지 조회 API")
    @GetMapping("/{image-id}")
    public ResponseEntity <byte[]> getReviewImages (@PathVariable("image-id")long imageId){
        log.info("---Finding Review Image---");
        ReviewImage reviewImage = reviewImageService.findReviewImage(imageId);
        byte[] imageArray = reviewImage.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(reviewImage.getType()));
        log.info("Found Review Image : {}", imageId);
        return new ResponseEntity<byte[]>(imageArray,headers, HttpStatus.OK);
    }
}
