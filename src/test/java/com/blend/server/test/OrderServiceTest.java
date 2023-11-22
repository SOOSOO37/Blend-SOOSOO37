package com.blend.server.test;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.order.Order;
import com.blend.server.order.OrderRepository;
import com.blend.server.order.OrderService;
import com.blend.server.orderproduct.OrderProduct;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        @Test
        public void testCreateOrder() {

            Order order = TestObjectFactory.createOrder();
            Product product = TestObjectFactory.createProduct();

            List<OrderProduct> orderProductList = new ArrayList<>();
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setQuantity(2);
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setTrackingNumber("123456");
            orderProduct.setOrderProductStatus(OrderProduct.OrderProductStatus.PAY_FINISH);

            order.setOrderProductList(orderProductList);

            when(productService.findVerifiedProduct(anyLong())).thenReturn(product);

            when(orderRepository.save(order)).thenReturn(order);

            Order createdOrder = orderService.createOrder(order);

            assertNotNull(createdOrder);
            assertEquals(order, createdOrder);
        }

    @Test
    public void testCalculateTotalPrice() {

        List<OrderProduct> orderProductList = new ArrayList<>();
        Product product =TestObjectFactory.createProduct();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(2);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        // calculateTotalPrice 메서드 호출
        int totalPrice = orderService.calculateTotalPrice(orderProductList);

        // 예상 총 가격과 비교
        int expectedTotalPrice = (50000 * 2); // 예상 총 가격 계산
        assertEquals(expectedTotalPrice, totalPrice);
    }

}



