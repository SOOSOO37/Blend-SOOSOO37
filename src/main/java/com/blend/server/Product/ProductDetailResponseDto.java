package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetailResponseDto {

    private long id;

    private String brand;

    private String productName;

    private String category;

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

}
