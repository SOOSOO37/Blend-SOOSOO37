package com.blend.server.orderproduct;

import com.blend.server.product.ProductResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductDetailResponseDto {

    private long quantity;

    private String parcelNumber;

    private OrderProduct.OrderProductStatus orderProductStatus;

    private ProductResponseDto productResponse;
}
