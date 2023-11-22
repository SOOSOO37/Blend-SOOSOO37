package com.blend.server.seller;

import com.blend.server.Product.ProductRepository;
import com.blend.server.Product.ProductResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
