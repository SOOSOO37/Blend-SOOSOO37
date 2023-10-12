package com.blend.server.Product;

import com.blend.server.category.Category;
import com.blend.server.global.audit.Auditable;
import com.blend.server.orderproduct.OrderProduct;

import com.blend.server.productImage.ProductImage;
import com.blend.server.seller.Seller;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
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

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonManagedReference
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @JsonBackReference
    @OneToMany(mappedBy = "product",cascade = CascadeType.REMOVE)
    private List<OrderProduct> orderProductList = new ArrayList<>();

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
    private String info;

    @Column(nullable = false)
    private String sizeInfo;

    @Enumerated(value = EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.SALE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImages = new ArrayList<>();

    public enum ProductStatus{

        SALE(1,"판매 중"),

        INSTOCK(2,"재고 5개 미만"),

        SOLDOUT(3,"품절"),

        PRODUCT_DELETE(4,"삭제상품");

        @Getter
        public int statusNumber;

        @Getter
        public String statusDescription;

        ProductStatus(int statusNumber, String statusDescription) {
            this.statusNumber = statusNumber;
            this.statusDescription = statusDescription;
        }
    }
    public void addProductImage(ProductImage productImage){
        this.productImages.add(productImage);
        if(productImage.getProduct() != this){
            productImage.addProduct(this);
        }
    }

}
