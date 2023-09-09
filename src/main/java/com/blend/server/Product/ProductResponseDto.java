package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDto {

    private long id;

    private String brand;

    private String productName;

    private String category;

    private int ranking;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
