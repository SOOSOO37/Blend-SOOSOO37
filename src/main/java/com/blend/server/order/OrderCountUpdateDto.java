package com.blend.server.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCountUpdateDto {

    private int newQuantity;
}
