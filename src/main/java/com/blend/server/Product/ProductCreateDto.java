package com.blend.server.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

    private String brand;

    private String productName;

    private int price;

    private int salePrice;

    private Product.ProductStatus productStatus;

    private int reviewCount;

    private int likeCount;

    private int productCount;

    private String info;

    private String sizeInfo;

}
