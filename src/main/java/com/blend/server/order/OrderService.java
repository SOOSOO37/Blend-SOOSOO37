package com.blend.server.order;

import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.cart.Cart;
import com.blend.server.cart.CartProductRepository;
import com.blend.server.cart.CartService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;

    private final CartProductRepository cartProductRepository;

    public Order createOrder(Order order, User user) {
        log.info("---Creating Order---");

        if(user == null) {
            log.error("User not found");
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        // 주문시 상품수량 -
        List<OrderProduct> orderProductList = minusProductCount(order);
        order.setOrderProductList(orderProductList);

        // 상품 총합금액 계산
        int totalPrice = calculateTotalPrice(orderProductList);
        order.setTotalPrice(totalPrice);
        order.setUser(user);

        Order savedOrder = orderRepository.save(order);
        emptyCart(user);

        log.info("Created Order : OrderId: {}, User: {}", savedOrder.getId(), user.getId());

        return savedOrder;
    }

    public int calculateTotalPrice(List<OrderProduct> orderProductList){
        int totalPrice = 0;
        for (OrderProduct orderProduct : orderProductList){
            int price = orderProduct.getProduct().getPrice();
            long productCount = orderProduct.getQuantity();

            totalPrice += price * productCount;
        }
        log.debug("Calculated total price : {}", totalPrice);
        return totalPrice;
    }

    public List<OrderProduct> minusProductCount(Order order){
        List<OrderProduct> orderProductList = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    Product product = productService.findVerifiedProduct(orderProduct.getProduct().getId());

                    // 주문 상품의 수량을 기준으로 상품 수량 감소 및 체크
                    if (product.getProductCount() < orderProduct.getQuantity()) {
                        log.warn("Product quantity exceeded : ProductId: {}, OrderProductId: {}", product.getId(), orderProduct.getOrderProductId());
                        throw new BusinessLogicException(ExceptionCode.QUANTITY_EXCEEDED);
                    }

                    long minusCount = product.getProductCount() - orderProduct.getQuantity();
                    product.setProductCount((int) minusCount);
                    orderProduct.setProduct(product);
                    return orderProduct;
                })
                .collect(Collectors.toList());
        log.info("Updated product counts : OrderId: {}", order.getId());
        return orderProductList;
    }

    public Order updateOrder(User user, Order order){
        log.info("---Updating Order---");
        long orderId = order.getId();
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId,user.getId());
        BeanUtils.copyProperties(order,findOrder,"user","totalPrice");
        Order updatedOrder = orderRepository.save(findOrder);
        log.info("Updated Order : OrderId: {}, UserId: {}", orderId, user.getId());

        return updatedOrder;
    }

    public Order updateQuantity(User user, long orderId, long orderProductId, long newQuantity) {
        log.info("Updating Order Quantity - OrderId: {}, OrderProductId: {}, NewQuantity: {}", orderId, orderProductId, newQuantity);
        Order order = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());

        OrderProduct orderProduct = findOrderProduct(order, orderProductId);
        Product product = orderProduct.getProduct();

        // 이전 수량과 현재 수량 차이 계산
        long quantityDifference = newQuantity - orderProduct.getQuantity();

        if (quantityDifference > product.getProductCount()) {
            log.warn("Product quantity exceeded : OrderId: {}, OrderProductId: {}, NewQuantity: {}", orderId, orderProductId, newQuantity);
            throw new BusinessLogicException(ExceptionCode.QUANTITY_EXCEEDED);
        }
        long newProductCount = product.getProductCount() - quantityDifference;

        product.setProductCount((int) newProductCount);

        orderProduct.setQuantity(newQuantity);

        order.setTotalPrice(calculateTotalPrice(order.getOrderProductList()));
        Order updatedOrder = orderRepository.save(order);

        log.info("Updated Order Quantity : OrderId: {}, OrderProductId: {}, NewQuantity: {}", orderId, orderProductId, newQuantity);

        return updatedOrder;
    }

    private OrderProduct findOrderProduct(Order order, long orderProductId) {
        log.debug("Finding OrderProduct : OrderId: {}, OrderProductId: {}", order.getId(), orderProductId);
        return order.getOrderProductList().stream()
                .filter(op -> op.getOrderProductId() == orderProductId)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No order product found : {}. OrderId: {}", orderProductId, order.getId());
                    return new EntityNotFoundException("No order product found with ID: " + orderProductId);
                });
    }

    public Order findOrder (User user,long orderId){
        log.info("Finding Order - OrderId: {}, User: {}", orderId, user.getId());
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());
        log.info("Found Order - OrderId: {}, User: {}", orderId, user.getId());
        return findOrder;
    }

    public Order cancelOrder (User user,long orderId){
        log.info("Cancelling Order - OrderId: {}, User: {}", orderId, user.getId());
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());
        findOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCEL);
        Order cancelledOrder = orderRepository.save(findOrder);
        log.info("cancelled Order - OrderId: {}, User: {}", orderId, user.getId());

        return cancelledOrder;
    }
    //사용자 전체 주문 조회
    public Page<Order> findAllOrder (int page, int size, User user){
        log.info("Finding All Orders - User: {}, Page: {}, Size: {}", user.getId(), page, size);
        Page<Order> orderPage = orderRepository.findByUser(user, PageRequest.of(page, size, Sort.by("id").descending()));
        log.info("User Orders - User: {}, Page: {}, Size: {}, Total Orders: {}",
                user.getId(), page, size, orderPage.getTotalElements());

        return orderPage;

    }

    public Order findVerifiedOrder (long id){
        log.info("---Finding Verified Order---");
        Optional<Order> findOrder = orderRepository.findById(id);
        Order order =
                findOrder.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        log.info("Verified Order: {}", id);
        return order;
    }

    // 셀러에도 추가
    public void verifyOrderUser(long orderId, long userId){
        log.info("---Verifying User---");
        Order findOrder = findVerifiedOrder(orderId);
        long dbUserId = findOrder.getUser().getId();

        if(userId != dbUserId){
            log.warn("User{} is not authorized to access Order {}", userId, orderId);
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        log.info("Verified User : {}, Order ID: {}", userId, orderId);
    }
    public void emptyCart(User user){
        log.info("Emptying Cart - User: {}", user.getId());
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartProductRepository.deleteAllByCartId(cartId);
        log.info("Emptied Cart  - CartId: {}, User: {}", cartId, user.getId());
    }
}
