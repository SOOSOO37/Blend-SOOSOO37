package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private long id;

    private String brand;

    private String productName;

    private long categoryId;

    private String name;

    private int ranking;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private String image;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;



}
