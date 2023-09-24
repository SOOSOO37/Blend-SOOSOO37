package com.blend.server.Product;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetailResponseDto {

    private long id;

    private String brand;

    private String productName;

    private long categoryId;

    private String name;

    private int ranking;

    private int viewCount;

    private int reviewCount;

    private int likeCount;

    private int productCount;

    private int price;

    private int salePrice;

    private String image;

    private String info;

    private String sizeInfo;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ProductDetailResponseDto(long id, String brand, String productName, long categoryId,
                                    String name, int ranking, int viewCount, int reviewCount,
                                    int likeCount, int productCount, int price, int salePrice,
                                    String image, String info, String sizeInfo,
                                    LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.brand = brand;
        this.productName = productName;
        this.categoryId = categoryId;
        this.name = name;
        this.ranking = ranking;
        this.viewCount = viewCount;
        this.reviewCount = reviewCount;
        this.likeCount = likeCount;
        this.productCount = productCount;
        this.price = price;
        this.salePrice = salePrice;
        this.image = image;
        this.info = info;
        this.sizeInfo = sizeInfo;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
