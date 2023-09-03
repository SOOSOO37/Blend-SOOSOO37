package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class ProductPostDto {

    private String brand;

    private String productName;

    private String category;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private String image;

    private int reviewCount;

    private int likeCount;

    private int productCount;

    private String info;

    private String sizeInfo;

    private LocalDateTime createdAt;


}
