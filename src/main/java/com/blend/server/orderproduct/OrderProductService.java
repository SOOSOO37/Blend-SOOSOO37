package com.blend.server.orderproduct;

import com.blend.server.Product.Product;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.blend.server.orderproduct.OrderProduct.OrderProductStatus.*;

@Transactional
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    public OrderProduct updateOrderStatus(long orderProductId, OrderProductPatchDto orderProductPatchDto) {

        OrderProduct orderProduct = findVerifiedOrderProduct(orderProductId);

        changeStatus(orderProduct, orderProductPatchDto.getOrderProductStatus());

        isStatusTransition(orderProduct.getOrderProductStatus(), orderProductPatchDto.getOrderProductStatus());

        postTrackingNumber(orderProduct, orderProductPatchDto.getTrackingNumber());

        return orderProduct;
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

    // 상태 변경 후 저장
    public void changeStatus (OrderProduct orderProduct, OrderProduct.OrderProductStatus newStatus) {
        OrderProduct.OrderProductStatus currentStatus = orderProduct.getOrderProductStatus();

        if (isStatusTransition(currentStatus, newStatus)) {
            orderProduct.setOrderProductStatus(newStatus);
            orderProductRepository.save(orderProduct);
        } else {
            throw new BusinessLogicException(ExceptionCode.DO_NOT_NEXTSTEP);
        }
    }

    // 주문 상품 상태 변경 설정(한 단계씩 변경가능)
    private boolean isStatusTransition(OrderProduct.OrderProductStatus currentStatus, OrderProduct.OrderProductStatus newStatus) {

        switch (currentStatus) {
            case PAY_STANDBY:
                return newStatus == OrderProduct.OrderProductStatus.PAY_FINISH;
            case PAY_FINISH:
                return newStatus == OrderProduct.OrderProductStatus.DELIVERY_PROCESS;
            case DELIVERY_PROCESS:
                return newStatus == OrderProduct.OrderProductStatus.IN_DELIVERY;
            case IN_DELIVERY:
                return newStatus == OrderProduct.OrderProductStatus.DELIVERED;
            default:
                return false;
        }
    }










}
