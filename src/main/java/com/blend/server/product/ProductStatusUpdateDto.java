package com.blend.server.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductStatusUpdateDto {

    private long id;

    private Product.ProductStatus productStatus;


}
