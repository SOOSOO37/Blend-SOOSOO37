package com.blend.server.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPostDto {

    private long userId;

    private long productId;

    private String title;

    private String content;

    private int score;
}
