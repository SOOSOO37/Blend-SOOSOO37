package com.blend.server.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPatchDto {

    private long id;

    private String title;

    private String content;

    private int score;
}
