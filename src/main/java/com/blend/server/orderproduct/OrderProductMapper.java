package com.blend.server.orderproduct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderProductMapper {

    List<OrderProductSellerResponseDto> orderProductListToOrderProcutSellerResponseDtos (List<OrderProduct> orderProductList);

    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "order.totalPrice", target = "totalPrice")
    @Mapping(source = "order.createdAt", target = "createdAt")
    @Mapping(source = "order.receiver", target = "receiver")
    @Mapping(source = "order.phoneNumber", target = "phoneNumber")
    @Mapping(source = "order.deliveryAddress", target = "deliveryAddress")
    OrderProductSellerResponseDto orderProductToOrderSellerResponseDto (OrderProduct orderProduct);
}
