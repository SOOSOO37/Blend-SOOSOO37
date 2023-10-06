package com.blend.server.order;

import com.blend.server.orderproduct.OrderProductDetailResponseDto;
import com.blend.server.orderproduct.OrderProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {

    private long id;

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

    private int totalPrice;

    private Order.OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
