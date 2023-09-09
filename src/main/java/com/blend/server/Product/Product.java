package com.blend.server.Product;

import com.blend.server.Product.global.audit.Auditable;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(value = EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.SALE;


    public enum ProductStatus{

        SALE(1,"판매 중"),

        INSTOCK(2,"재고 5개 미만"),

        SOLDOUT(3,"품절");

        @Getter
        public int statusNumber;

        @Getter
        public String statusDescription;

        ProductStatus(int statusNumber, String statusDescription) {
            this.statusNumber = statusNumber;
            this.statusDescription = statusDescription;
        }


    }

}
