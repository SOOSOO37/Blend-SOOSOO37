package com.blend.server.productImage;

import com.blend.server.Product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    private String type;

    // 이미지와 연관된 상품 정보 (다대일 관계)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public void addProduct(Product product) {
        this.product = product;
        if (hasProductImage()) {
            this.product.addProductImage(this);
        }
    }
    private boolean hasProductImage() {
        return !product.getProductImages().contains(this);
    }

}

