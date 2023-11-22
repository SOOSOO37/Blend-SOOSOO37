package com.blend.server.reviewImage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/review-images")
@RestController
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @GetMapping("/{image-id}")
    public ResponseEntity <byte[]> getReviewImages (@PathVariable("image-id")long imageId){
        ReviewImage reviewImage = reviewImageService.findReviewImage(imageId);
        byte[] imageArray = reviewImage.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(reviewImage.getType()));
        return new ResponseEntity<byte[]>(imageArray,headers, HttpStatus.OK);
    }
}
