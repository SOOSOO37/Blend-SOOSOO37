package com.blend.server.order;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.orderproduct.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Order createOrder(Order order) {
        // 주문시 상품수량 -
        List<OrderProduct> orderProductList = minusProductCount(order);
        order.setOrderProductList(orderProductList);

        // 상품 총합금액 계산
        int totalPrice = calculateTotalPrice(orderProductList);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
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
                    long minusCount = product.getProductCount() - orderProduct.getQuantity();
                    product.setProductCount((int) minusCount);
                    orderProduct.setProduct(product);
                    return  orderProduct;
                })
                .collect(Collectors.toList());
        return orderProductList;
    }

    public Order updateOrder(long id, Order order){
        Order findOrder = findVerifiedOrder(id);

        BeanUtils.copyProperties(order,findOrder);

        return orderRepository.save(findOrder);
    }

    public Order findOrder (long id){
        Order findOrder = findVerifiedOrder(id);
        return findOrder;
    }

    public Order cancelOrder (long id){
        Order findOrder = findVerifiedOrder(id);

        findOrder.setOrderStatus(Order.OrderStatus.ORDER_CANCEL);

        return orderRepository.save(findOrder);
    }
    //사용자 전체 주문 조회
    public Page<Order> findAllOrder (int page, int size){

        return orderRepository.findAll(PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Order findVerifiedOrder (long id){
        Optional<Order> findOrder = orderRepository.findById(id);

        Order order =
                findOrder.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
        return order;
    }
}
