package com.blend.server.cart;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CartResponseDto {

    private long cartId;

    private List<CartProductResponseDto> cartProductList;
}
