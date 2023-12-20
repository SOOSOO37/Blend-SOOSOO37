package com.blend.server.orderproduct;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.Order;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.blend.server.orderproduct.OrderProduct.OrderProductStatus.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    private final SellerService sellerService;

    public OrderProduct updateOrderStatus(long orderProductId, OrderProductUpdateDto orderProductUpdateDto,Seller seller) {
        log.info("---Updating OrderProduct---");
        OrderProduct orderProduct = findVerifiedOrderProduct(orderProductId);

        verifySeller(orderProductId,seller.getId());

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
            log.info("OrderProduct status changed  OrderProductId: {}, NewStatus: {}", orderProduct.getOrderProductId(), newStatus);
        } else {
            log.warn("Invalid status transition {}, CurrentStatus: {}, NewStatus: {}", orderProduct.getOrderProductId(), currentStatus, newStatus);
            throw new BusinessLogicException(ExceptionCode.DO_NOT_NEXTSTEP);
        }
    }

    // 주문 상품 상태 변경 설정(한 단계씩 변경 가능)
    private boolean isStatusTransition(OrderProduct.OrderProductStatus currentStatus, OrderProduct.OrderProductStatus newStatus) {
        int currentStatusNum = currentStatus.getNumber();
        int newStatusNum = newStatus.getNumber();

        return newStatusNum == currentStatusNum + 1;
    }

    //
    // 판매자 전체 주문 조회
    public Page<OrderProduct> findAllOrderProductBySeller (int page, int size, Seller seller){
        Seller findSeller = sellerService.findVerifiedSeller(seller.getId());
        Page<OrderProduct> orderProducts = orderProductRepository.findByProductSeller(
                findSeller, PageRequest.of(page, size, Sort.by("order.createdAt").descending()));

        log.info("Seller OrderProducts - Seller ID: {}, Page: {}, Page size: {}, Total orders: {}", findSeller.getId(), page, size, orderProducts.getTotalElements());

        return orderProducts;
    }


    public OrderProduct findVerifiedOrderProduct(long orderProductId){

        Optional<OrderProduct> optionalOrderProduct =
                orderProductRepository.findById(orderProductId);
        OrderProduct findOrderProduct =
                optionalOrderProduct.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        log.info("Verified OrderProduct : {}", orderProductId);
        return findOrderProduct;

    }

    //주문상품 운송장 번호 등록
    public void postTrackingNumber(OrderProduct orderProduct, String trackingNumber){
        if (orderProduct.getOrderProductStatus() == DELIVERY_PROCESS) {
            orderProduct.setTrackingNumber(trackingNumber);
            orderProductRepository.save(orderProduct);
            log.info("Tracking number posted : OrderProductId: {}, TrackingNumber: {}", orderProduct.getOrderProductId(), trackingNumber);
        } else {
            log.warn("Cannot post tracking number : OrderProductId: {}, OrderProductStatus: {}", orderProduct.getOrderProductId(), orderProduct.getOrderProductStatus());
        }
    }

    public void verifySeller(long orderProductId, long sellerId){
        log.info("---Verifying Seller---");
        OrderProduct findOrderProduct = findVerifiedOrderProduct(orderProductId);
        long dbSellerId = findOrderProduct.getProduct().getSeller().getId();

        if(sellerId != dbSellerId){
            log.warn("Seller{} is not authorized to access OrderProduct {}", sellerId, orderProductId);
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
        }
        log.info("Verified Seller : {}, OrderProduct ID: {}", sellerId, orderProductId);
    }

}
