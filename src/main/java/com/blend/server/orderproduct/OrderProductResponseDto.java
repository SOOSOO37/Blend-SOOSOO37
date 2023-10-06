package com.blend.server.orderproduct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OrderProductResponseDto {

    private long id;

    private long quantity;

    private String productName;

    private long price;
}
