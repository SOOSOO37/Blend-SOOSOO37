package com.blend.server.product;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ProductResponseDto(long id, String brand, String productName, long categoryId,
                              String name, int ranking, int price, int salePrice,
                              Product.ProductStatus productStatus,
                              LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.brand = brand;
        this.productName = productName;
        this.categoryId = categoryId;
        this.name = name;
        this.ranking = ranking;
        this.price = price;
        this.salePrice = salePrice;
        this.productStatus = productStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
