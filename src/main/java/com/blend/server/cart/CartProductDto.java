package com.blend.server.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class CartProductDto {

    private Long productId;
    private int productCount;
}
