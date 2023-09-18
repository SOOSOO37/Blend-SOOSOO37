package com.blend.server.orderproduct;

import com.blend.server.Product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDto {

    private long productId;

    private int quantity;


}
