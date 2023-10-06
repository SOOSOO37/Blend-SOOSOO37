package com.blend.server.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderUpdateDto {

    private long id;

    private String receiver;

    private String deliveryAddress;

    private int phoneNumber;

    private String payMethod;

}
