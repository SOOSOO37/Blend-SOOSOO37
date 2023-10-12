package com.blend.server.seller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class SellerPostDto {

    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$",
            message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$",
            message = "비밀번호는 숫자, 문자를 포함하여 8~20자리여야 합니다.")
    private String password;

    @NotBlank(message = "이름은 공백이 아니어야 합니다.")
    private String name;

    @NotBlank(message = "사업자등록번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리의 숫자입니다.")
    private String regNumber;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{8,12}$", message = "전화번호는 최소 8자리에서 최대 12자리의 숫자입니다.")
    private String phone;

    @NotBlank(message = "은행명은 필수 입력 값입니다.")
    private String bank;

    @NotBlank(message = "계좌번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{10,14}$", message = "계좌번호는 최소 10자리에서 최대 14자리의 숫자입니다.")
    private String accountNumber;
}
