package com.blend.server.order;

import com.blend.server.orderproduct.OrderProductDetailResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponseDto {

    private long id;

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

    private int totalPrice;

    private Order.OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private List<OrderProductDetailResponseDto> orderProductList;
}
