package com.blend.server.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminSellerPatchDto {

    private Long id;

    private String email;

    private String password;

    private String address;

    private String phone;
}
