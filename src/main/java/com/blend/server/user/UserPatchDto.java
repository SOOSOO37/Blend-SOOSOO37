package com.blend.server.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
public class UserPatchDto {

    private long id;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$",
            message = "비밀번호는 숫자, 문자를 포함하여 8~20자리여야 합니다.")
    private String password;

    private String nickName;
}
