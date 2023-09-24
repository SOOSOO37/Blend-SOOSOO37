package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ProductCreateDto {

    private String brand;

    private String productName;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private String image;

    private int reviewCount;

    private int likeCount;

    private int productCount;

    private String info;

    private String sizeInfo;




}
