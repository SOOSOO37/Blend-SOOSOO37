package com.blend.server.Product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;

@Getter
@Setter
public class Product {

    private long id;

    private String brand;

    private String productName;

    private String category;

    private int ranking;

    private int viewCount;

    private int reviewCount;

    private int likeCount;

    private int price;

    private int salePrice;

    private String image;

    private String info;

    private String sizeInfo;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Product(String brand, String productName, String category, int likeCount, int price, String info, String sizeInfo, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.brand = brand;
        this.productName = productName;
        this.category = category;
        this.likeCount = likeCount;
        this.price = price;
        this.info = info;
        this.sizeInfo = sizeInfo;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
