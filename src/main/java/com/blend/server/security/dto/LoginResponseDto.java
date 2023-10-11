package com.blend.server.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginResponseDto {
    private long id;
    private String email;
    private String nickName;

    @Builder
    public LoginResponseDto(long id, String email, String nickName) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }
}
