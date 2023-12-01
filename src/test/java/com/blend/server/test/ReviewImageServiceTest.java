package com.blend.server.test;

import com.blend.server.review.Review;
import com.blend.server.reviewImage.ReviewImage;
import com.blend.server.reviewImage.ReviewImageRepository;
import com.blend.server.reviewImage.ReviewImageService;
import com.blend.server.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewImageServiceTest {

    @InjectMocks
    private ReviewImageService reviewImageService;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("리뷰 이미지 조회 테스트")
    @Test
    public void ReviewImageTest() {

        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setId(1L);

        when(reviewImageRepository.findById(reviewImage.getId())).thenReturn(Optional.of(reviewImage));

        ReviewImage result = reviewImageService.findReviewImage(reviewImage.getId());

        assertNotNull(result);
        assertEquals(reviewImage.getId(), result.getId());
    }
}
