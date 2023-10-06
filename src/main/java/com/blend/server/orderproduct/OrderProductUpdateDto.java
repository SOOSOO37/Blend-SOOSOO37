package com.blend.server.orderproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderProductUpdateDto {

    private String trackingNumber;

    private OrderProduct.OrderProductStatus orderProductStatus;
}
