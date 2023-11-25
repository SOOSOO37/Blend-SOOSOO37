package com.blend.server.seller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SellerResponseDto {

    private long id;

    private String email;

    private String password;

    private String name;

    private String regNumber;

    private String address;

    private String phone;

    private String bank;

    private String accountNumber;

}
