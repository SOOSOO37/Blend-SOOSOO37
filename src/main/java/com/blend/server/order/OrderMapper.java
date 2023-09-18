package com.blend.server.order;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductMapper;
import com.blend.server.Product.ProductResponseDto;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.orderproduct.OrderProductDetailResponseDto;
import com.blend.server.orderproduct.OrderProductDto;
import org.aspectj.weaver.ast.Or;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper (componentModel = "spring")
public interface OrderMapper {
    default Order orderPostDtoToOrder (OrderPostDto orderPostDto){
        Order order = new Order();
        order.setReceiver(orderPostDto.getReceiver());
        order.setDeliveryAddress(orderPostDto.getDeliveryAddress());
        order.setPhoneNumber(orderPostDto.getPhoneNumber());
        order.setPayMethod(orderPostDto.getPayMethod());
        order.setTotalPrice(orderPostDto.getTotalPrice());
        order.setTotalPrice(orderPostDto.getTotalPrice());

        List<OrderProduct> orderProducts = orderPostDto.getOrderProductList().stream()
                .map(orderProductDto -> {
                    OrderProduct orderProduct = new OrderProduct();
                    Product product = new Product();
                    product.setId(orderProductDto.getProductId());
                    orderProduct.setOrder(order);
                    orderProduct.setProduct(product);
                    orderProduct.setQuantity(orderProductDto.getQuantity());
                    return orderProduct;
                }).collect(Collectors.toList());

        order.setOrderProductList(orderProducts);
        return order;
    }

    Order orderPatchDtoToOrder(OrderPatchDto orderPatchDto);

    OrderResponseDto orderToOrderResponseDto (Order order);

    default OrderDetailResponseDto orderToOrderDetailResponse(Order order){

        OrderDetailResponseDto orderDetailResponseDto = new OrderDetailResponseDto();

        orderDetailResponseDto.setId(order.getId());
        orderDetailResponseDto.setReceiver(order.getReceiver());
        orderDetailResponseDto.setPhoneNumber(order.getPhoneNumber());
        orderDetailResponseDto.setDeliveryAddress(order.getDeliveryAddress());
        orderDetailResponseDto.setOrderStatus(order.getOrderStatus());
        orderDetailResponseDto.setTotalPrice(order.getTotalPrice());
        orderDetailResponseDto.setPayMethod(order.getPayMethod());
        orderDetailResponseDto.setCreatedAt(order.getCreatedAt());
        orderDetailResponseDto.setModifiedAt(order.getModifiedAt());

        ProductResponseDto productResponseDto = new ProductResponseDto();
        List<OrderProductDetailResponseDto> orderProductDetailResponse = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    OrderProductDetailResponseDto orderProductDetailResponseDto = new OrderProductDetailResponseDto();
                    orderProductDetailResponseDto.setQuantity(orderProduct.getQuantity());
                    orderProductDetailResponseDto.setParcelNumber(orderProduct.getTrackingNumber());
                    orderProductDetailResponseDto.setOrderProductStatus(orderProduct.getOrderProductStatus());
                    orderProductDetailResponseDto.setProductResponse(ProductMapper.productToProductResponseDto(orderProduct.getProduct()));
                    return orderProductDetailResponseDto;
                }).collect(Collectors.toList());
        orderDetailResponseDto.setOrderProductList(orderProductDetailResponse);

        return orderDetailResponseDto;
    }

    List<OrderResponseDto> ordersToOrderResponseDtos(List<Order> orderList);






}
