package com.blend.server.review;

import com.blend.server.reviewImage.ReviewImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDetailResponseDto {

    private long id;

    private String title;

    private String content;

    private int score;
    private Review.ReviewStatus reviewStatus;

    private List<String> reviewImageUrls;

    private LocalDateTime createdAt;

    public String getReviewStatus(){
        return reviewStatus.getDescription();
    }

    @Builder
    public ReviewDetailResponseDto(long id, String title, String content,
                                   int score, Review.ReviewStatus reviewStatus,
                                   List<String> reviewImageUrls, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.score = score;
        this.reviewStatus = reviewStatus;
        this.reviewImageUrls = reviewImageUrls;
        this.createdAt = createdAt;
    }
}
