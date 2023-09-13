package com.blend.server.Product.product;

import com.blend.server.Product.category.Category;
import com.blend.server.Product.global.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String productName;

    //@Column(nullable = false)
    //private String category;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonManagedReference
    @JoinColumn(name = "category_id")
    private Category category;

    private int ranking;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int productCount;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String info;

    @Column(nullable = false)
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
