package com.blend.server.review;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewResponseDto {

    private long id;

    private String title;

    private String content;

    private int score;

}
