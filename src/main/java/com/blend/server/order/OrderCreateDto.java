package com.blend.server.order;

import com.blend.server.orderproduct.OrderProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderCreateDto {

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

    private int totalPrice;

    private List<OrderProductDto> orderProductList;

}
