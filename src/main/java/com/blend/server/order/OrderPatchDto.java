package com.blend.server.order;

import com.blend.server.orderproduct.OrderProductDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderPatchDto {

    private long id;

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

}
