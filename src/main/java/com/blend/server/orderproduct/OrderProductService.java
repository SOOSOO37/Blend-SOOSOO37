package com.blend.server.orderproduct;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.blend.server.orderproduct.OrderProduct.OrderProductStatus.*;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProduct updateOrderStatus(long orderProductId, OrderProductUpdateDto orderProductUpdateDto) {

        OrderProduct orderProduct = findVerifiedOrderProduct(orderProductId);

        changeStatus(orderProduct, orderProductUpdateDto.getOrderProductStatus());

        isStatusTransition(orderProduct.getOrderProductStatus(), orderProductUpdateDto.getOrderProductStatus());

        postTrackingNumber(orderProduct, orderProductUpdateDto.getTrackingNumber());

        return orderProduct;
    }

    public void changeStatus(OrderProduct orderProduct, OrderProduct.OrderProductStatus newStatus) {
        OrderProduct.OrderProductStatus currentStatus = orderProduct.getOrderProductStatus();
        if (isStatusTransition(currentStatus, newStatus)) {
            orderProduct.setOrderProductStatus(newStatus);
            orderProductRepository.save(orderProduct);
        } else {
            throw new BusinessLogicException(ExceptionCode.DO_NOT_NEXTSTEP);
        }
    }

    // 주문 상품 상태 변경 설정(한 단계씩 변경 가능)
    private boolean isStatusTransition(OrderProduct.OrderProductStatus currentStatus, OrderProduct.OrderProductStatus newStatus) {
        // 각 상태 전환을 숫자로 표현하여 비교합니다.
        int currentStatusNum = currentStatus.getNumber();
        int newStatusNum = newStatus.getNumber();

        // 상태 전환 가능 여부를 판단합니다.
        return newStatusNum == currentStatusNum + 1;
    }


    // 판매자 전체 주문 조회
    public Page<OrderProduct> findAllOrderProduct (int page, int size){

        return orderProductRepository.findAll(PageRequest.of(page,size, Sort.by("order.createdAt").descending()));
    }


    public OrderProduct findVerifiedOrderProduct(long orderProductId){

        Optional<OrderProduct> optionalOrderProduct =
                orderProductRepository.findById(orderProductId);
        OrderProduct findOrderProduct =
                optionalOrderProduct.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        return findOrderProduct;

    }

    //주문상품 운송장 번호 등록
    public void postTrackingNumber(OrderProduct orderProduct, String trackingNumber){

        if(orderProduct.getOrderProductStatus() == DELIVERY_PROCESS)
            orderProduct.setTrackingNumber(trackingNumber);
        orderProductRepository.save(orderProduct);
    }

}
