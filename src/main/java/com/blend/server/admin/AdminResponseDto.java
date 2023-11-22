package com.blend.server.admin;

import com.blend.server.seller.Seller;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminResponseDto {

    private String email;

    private String name;

    private String regNumber;

    private String address;

    private String phone;

    private String bank;

    private String accountNumber;

    private Seller.SellerStatus sellerStatus;

}
