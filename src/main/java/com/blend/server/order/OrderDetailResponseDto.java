package com.blend.server.order;

import com.blend.server.global.audit.Auditable;
import com.blend.server.orderproduct.OrderProductDetailResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDetailResponseDto extends Auditable {

    private long id;

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

    private int totalPrice;

    private Order.OrderStatus orderStatus;

    private List<OrderProductDetailResponseDto> orderProductList;
}
