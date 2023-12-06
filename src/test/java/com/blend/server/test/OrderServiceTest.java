package com.blend.server.test;

import com.blend.server.cart.Cart;
import com.blend.server.cart.CartProductRepository;
import com.blend.server.cart.CartService;
import com.blend.server.category.Category;
import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.product.ProductService;
import com.blend.server.order.Order;
import com.blend.server.order.OrderRepository;
import com.blend.server.order.OrderService;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.review.Review;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private CartProductRepository cartProductRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        @Test
        public void testCreateOrder() {

            User user = new User();
            user.setId(1L);

            Cart cart = new Cart();
            cart.setId(1L);
            cart.setUser(user);

            Order order = TestObjectFactory.createOrder();
            order.setUser(user);

            Product product = TestObjectFactory.createProduct();
            product.setProductCount(10);
            product.setPrice(10000);

            List<OrderProduct> orderProductList = new ArrayList<>();
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setQuantity(2);
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);

            order.setOrderProductList(orderProductList);

            when(productService.findVerifiedProduct(product.getId())).thenReturn(product);
            when(cartService.findVerifiedCartByUser(user)).thenReturn(cart);


            when(orderRepository.save(order)).thenReturn(order);

            Order createdOrder = orderService.createOrder(order,user);

            assertNotNull(createdOrder);
            assertEquals(order, createdOrder);
            assertEquals(user.getCart().getCartProductList(),0);
        }

    @Test
    public void testCalculateTotalPrice() {

        List<OrderProduct> orderProductList = new ArrayList<>();
        Product product =TestObjectFactory.createProduct();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(2);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        int totalPrice = orderService.calculateTotalPrice(orderProductList);

        int expectedTotalPrice = (50000 * 2);
        assertEquals(expectedTotalPrice, totalPrice);
    }
    @DisplayName("상품 수량 감소 테스트")
    @Test
    public void minusProductCountTest() {

        Product product = TestObjectFactory.createProduct();
        product.setProductCount(10);
        product.setPrice(10000);

        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(2);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        Order order = new Order();
        order.setOrderProductList(orderProductList);

        when(productService.findVerifiedProduct(product.getId())).thenReturn(product);

        List<OrderProduct> result = orderService.minusProductCount(order);

        assertNotNull(result);
        assertEquals(8, result.get(0).getProduct().getProductCount());
    }
    @DisplayName("장바구니 상품 비우기 테스트")
    @Test
    public void emptyCartTest() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);

        when(cartService.findVerifiedCartByUser(user)).thenReturn(cart);

        orderService.emptyCart(user);

        verify(cartService).findVerifiedCartByUser(user);
        verify(cartProductRepository).deleteAllByCartId(cart.getId());
    }

    @DisplayName("주문 수령 정보 수정 테스트")
    @Test
    public void updateOrderTest(){
        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setUser(user);
        order.setId(1L);
        order.setReceiver("기존 수령인");
        order.setPhoneNumber(12345678);
        order.setDeliveryAddress("기존 주소");
        order.setPayMethod("무통장");

        Order updatedOrder = new Order();
        updatedOrder.setUser(user);
        updatedOrder.setId(1L);
        updatedOrder.setReceiver("수정 수령인");
        updatedOrder.setPhoneNumber(12345677);
        updatedOrder.setDeliveryAddress("수정 주소");
        updatedOrder.setPayMethod("카드 결제");

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(user,updatedOrder);

        assertNotNull(result);
        assertEquals(order.getId(),result.getId());
        assertEquals(updatedOrder.getDeliveryAddress(),result.getDeliveryAddress());
    }

    @DisplayName("주문 수량 수정 테스트")
    @Test
    public void updateQuantityTest(){
        User user = new User();
        user.setId(1L);

        // 상품 생성
        Product product = TestObjectFactory.createProduct();
        product.setProductCount(10);

        // 주문 상품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setQuantity(3);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        // 주문 생성
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderProductList(orderProductList);

        // Mock 객체 설정
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        long newQuantity = 5;

        Order result = orderService.updateQuantity(user, order.getId(), orderProduct.getOrderProductId(), newQuantity);

        assertNotNull(result);
        assertEquals(newQuantity, result.getOrderProductList().get(0).getQuantity());
        assertEquals(product.getProductCount(),result.getOrderProductList().get(0).getProduct().getProductCount());

    }
    @DisplayName("주문 조회 테스트")
    @Test
    public void findOrderTest (){
        User user = new User();
        user.setId(1L);

        // 상품 생성
        Product product = TestObjectFactory.createProduct();
        product.setProductCount(10);

        // 주문 상품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setQuantity(3);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        // 주문 생성
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderProductList(orderProductList);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Order result = orderService.findOrder(user,order.getId());

        assertNotNull(result);
        assertEquals(order.getId(),result.getId());
        assertEquals(order.getUser(),result.getUser());
        assertEquals(order.getOrderProductList(),result.getOrderProductList());
    }
    @DisplayName("주문 취소 테스트")
    @Test
    public void cancelOrderTest(){
        User user = new User();
        user.setId(1L);

        // 상품 생성
        Product product = TestObjectFactory.createProduct();
        product.setProductCount(10);

        // 주문 상품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setQuantity(3);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        // 주문 생성
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderProductList(orderProductList);
        order.setOrderStatus(Order.OrderStatus.ORDER_DONE);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.cancelOrder(user,order.getId());

        assertNotNull(result);
        assertEquals(order.getId(),result.getId());
        assertEquals(Order.OrderStatus.ORDER_CANCEL,result.getOrderStatus());

    }

    @DisplayName("사용자 전체 주문 조회 테스트")
    @Test
    public void findAllOrder(){
        User user = new User();
        user.setId(1L);

        // 상품 생성
        Product product = TestObjectFactory.createProduct();
        product.setProductCount(10);

        // 주문 상품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setQuantity(3);
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderProductList(orderProductList);
        order.setOrderStatus(Order.OrderStatus.ORDER_DONE);

        Order secondOrder = new Order();
        secondOrder.setId(1L);
        secondOrder.setUser(user);
        secondOrder.setOrderProductList(orderProductList);
        secondOrder.setOrderStatus(Order.OrderStatus.ORDER_DONE);
        orderList.add(order);
        orderList.add(secondOrder);

        when(orderRepository.findByUser(
                user,
                PageRequest.of(0, 10, Sort.by("id").descending())
        )).thenReturn(new PageImpl<>(orderList));

        Page<Order> result = orderService.findAllOrder(0,10,user);

        assertNotNull(result);
        assertEquals(orderList.size(),result.getContent().size());
    }

}



