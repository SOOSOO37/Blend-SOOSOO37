package com.blend.server.Product;

import lombok.AllArgsConstructor;
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
