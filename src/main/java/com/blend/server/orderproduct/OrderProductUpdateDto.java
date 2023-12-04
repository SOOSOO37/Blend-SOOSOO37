package com.blend.server.orderproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductUpdateDto {

    private String trackingNumber;

    private OrderProduct.OrderProductStatus orderProductStatus;

}
