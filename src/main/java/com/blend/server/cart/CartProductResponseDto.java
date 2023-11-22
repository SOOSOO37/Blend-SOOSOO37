package com.blend.server.cart;

import com.blend.server.product.ProductResponseDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CartProductResponseDto {

    private int productCount;

    private ProductResponseDto productResponseDto;
}
