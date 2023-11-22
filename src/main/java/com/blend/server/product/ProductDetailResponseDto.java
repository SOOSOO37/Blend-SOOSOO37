package com.blend.server.product;

import com.blend.server.global.audit.Auditable;
import com.blend.server.review.ReviewDetailResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetailResponseDto extends Auditable {

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
    private String info;

    private String sizeInfo;

    private List<String> imageLinks;

    private List<ReviewDetailResponseDto> reviewList;





    @Builder

    public ProductDetailResponseDto(long id, String brand, String productName, long categoryId,
                                    String name, int ranking, int viewCount, int reviewCount,
                                    int likeCount, int productCount, int price, int salePrice,
                                    String info, String sizeInfo, List<String> imageLinks,List<ReviewDetailResponseDto> reviewList) {
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
        this.info = info;
        this.sizeInfo = sizeInfo;
        this.imageLinks = imageLinks;
        this.reviewList = reviewList;
    }
}
