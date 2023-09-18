package com.blend.server.orderproduct;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class OrderProductPatchDto {

    private String trackingNumber;

    private OrderProduct.OrderProductStatus orderProductStatus;
}
