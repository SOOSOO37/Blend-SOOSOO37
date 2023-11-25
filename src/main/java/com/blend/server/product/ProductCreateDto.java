package com.blend.server.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

    private Long sellerId;

    private String brand;

    private String productName;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private int reviewCount;

    private int likeCount;

    private int productCount;

    private String info;

    private String sizeInfo;

}
