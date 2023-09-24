package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPatchDto {

    private long id;

    private String brand;

    private String productName;

    private long categoryId;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private int ranking;

    private int likeCount;

    private int productCount;

    private String image;

    private String info;

    private String sizeInfo;


}
