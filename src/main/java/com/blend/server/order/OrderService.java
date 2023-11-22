package com.blend.server.order;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.cart.Cart;
import com.blend.server.cart.CartProductRepository;
import com.blend.server.cart.CartService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;

    private final CartProductRepository cartProductRepository;

    public Order createOrder(Order order, User user) {

        if(user == null) {
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
        return savedOrder;
    }

    public int calculateTotalPrice(List<OrderProduct> orderProductList){
        int totalPrice = 0;
        for (OrderProduct orderProduct : orderProductList){
            int price = orderProduct.getProduct().getPrice();
            long productCount = orderProduct.getQuantity();

            totalPrice += price * productCount;
        }
        return totalPrice;
    }

    private List<OrderProduct> minusProductCount(Order order){
        List<OrderProduct> orderProductList = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    Product product = productService.findVerifiedProduct(orderProduct.getProduct().getId());

                    // 주문 상품의 수량을 기준으로 상품 수량 감소 및 체크
                    if (product.getProductCount() < orderProduct.getQuantity()) {
                        throw new BusinessLogicException(ExceptionCode.QUANTITY_EXCEEDED);
                    }

                    long minusCount = product.getProductCount() - orderProduct.getQuantity();
                    product.setProductCount((int) minusCount);
                    orderProduct.setProduct(product);
                    return orderProduct;
                })
                .collect(Collectors.toList());
        return orderProductList;
    }

    public Order updateOrder(User user, Order order){
        long orderId = order.getId();
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId,user.getId());

        BeanUtils.copyProperties(order,findOrder,"user","totalPrice");

        return orderRepository.save(findOrder);
    }

    public Order updateQuantity(User user, long orderId, long orderProductId, long newQuantity) {

        Order order = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());

        OrderProduct orderProduct = findOrderProduct(order, orderProductId);
        Product product = orderProduct.getProduct();

        // 이전 수량과 현재 수량 차이 계산
        long quantityDifference = newQuantity - orderProduct.getQuantity();

        if (quantityDifference > product.getProductCount()) {
            throw new BusinessLogicException(ExceptionCode.QUANTITY_EXCEEDED);
        }
        long newProductCount = product.getProductCount() - quantityDifference;

        product.setProductCount((int) newProductCount);

        orderProduct.setQuantity(newQuantity);

        order.setTotalPrice(calculateTotalPrice(order.getOrderProductList()));

        return orderRepository.save(order);
    }

    private OrderProduct findOrderProduct(Order order, long orderProductId) {
        return order.getOrderProductList().stream()
                .filter(op -> op.getOrderProductId() == orderProductId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ID에 해당하는 주문 상품을 찾을 수 없습니다: " + orderProductId));
    }

    public Order findOrder (User user,long orderId){
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());

        return findOrder;
    }

    public Order cancelOrder (User user,long orderId){
        Order findOrder = findVerifiedOrder(orderId);
        verifyOrderUser(orderId, user.getId());

        findOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCEL);

        return orderRepository.save(findOrder);
    }
    //사용자 전체 주문 조회
    public Page<Order> findAllOrder (int page, int size, User user){

        return orderRepository.findByUser(user,PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Order findVerifiedOrder (long id){
        Optional<Order> findOrder = orderRepository.findById(id);
        Order order =
                findOrder.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        return order;
    }

    // 셀러에도 추가
    public void verifyOrderUser(long orderId, long userId){
        Order findOrder = findVerifiedOrder(orderId);
        long dbUserId = findOrder.getUser().getId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }
    public void emptyCart(User user){
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartProductRepository.deleteAllByCartId(cartId);
    }
}
