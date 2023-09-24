package com.blend.server.orderproduct;

import com.blend.server.Product.ProductResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderProductDetailResponseDto {

    private long quantity;

    private String parcelNumber;

    private OrderProduct.OrderProductStatus orderProductStatus;

    private ProductResponseDto productResponse;
}
