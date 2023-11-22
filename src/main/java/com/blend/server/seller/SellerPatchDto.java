package com.blend.server.seller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
public class SellerPatchDto {

    private long id;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$",
            message = "비밀번호는 숫자, 문자를 포함하여 8~20자리여야 합니다.")
    private String password;

    private String name;

    private String address;

    @Pattern(regexp = "^\\d{8,12}$", message = "전화번호는 최소 8자리에서 최대 12자리의 숫자입니다.")
    private String phone;
}
