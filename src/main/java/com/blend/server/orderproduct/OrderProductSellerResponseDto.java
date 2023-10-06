package com.blend.server.orderproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderProductSellerResponseDto {

    private long orderProductId;

    private String productName;

    private int price;

    private int quantity;

    private int totalPrice;

    private String receiver;

    private String phoneNumber;

    private String deliveryAddress;

    private OrderProduct.OrderProductStatus orderProductStatus;

    private String trackingNumber;

    private LocalDateTime createdAt;
}
