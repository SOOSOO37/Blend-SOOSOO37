package com.blend.server.test;

import com.blend.server.cart.Cart;
import com.blend.server.product.Product;
import com.blend.server.category.Category;
import com.blend.server.order.Order;
import com.blend.server.user.User;


import java.io.IOException;
import java.util.List;

public class TestObjectFactory {

    public TestObjectFactory() throws IOException {
    }

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
        product.setReviewCount(0);
        product.setLikeCount(0);
        product.setProductCount(10);
        product.setInfo("제품 정보");
        product.setSizeInfo("사이즈 정보");
        return product;
    }
    public static Order createOrder() {
        Order order = new Order();
        order.setDeliveryAddress("배송주소");
        order.setPhoneNumber(1234567890);
        order.setPayMethod("무통장");
        order.setReceiver("받는 사람");
        order.setTotalPrice(100000);
        order.setOrderStatus(Order.OrderStatus.ORDER_DONE);
        return order;
    }

}
