package com.blend.server.test;

import com.blend.server.Product.Product;
import com.blend.server.category.Category;

public class TestObjectFactory {

    public static Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("상의");
        return category;
    }

    public static Product createProduct() {
        Product product = new Product();
        product.setProductName("제품명");
        product.setBrand("브랜드");
        product.setPrice(50000);
        product.setSalePrice(40000);
        product.setProductStatus(Product.ProductStatus.SALE);
        product.setImage("Image.PNG");
        product.setReviewCount(0);
        product.setLikeCount(0);
        product.setProductCount(10);
        product.setInfo("제품 정보");
        product.setSizeInfo("사이즈 정보");
        return product;
    }
}
