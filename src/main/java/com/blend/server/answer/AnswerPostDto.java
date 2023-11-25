package com.blend.server.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerPostDto {

    private String userEmail;

    private String content;
}
