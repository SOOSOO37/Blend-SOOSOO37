package com.blend.server.order;

import com.blend.server.product.Product;
import com.blend.server.product.ProductMapper;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.orderproduct.OrderProductDetailResponseDto;
import org.springframework.beans.BeanUtils;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper (componentModel = "spring")
public interface OrderMapper {
    default Order orderPostDtoToOrder (OrderCreateDto orderCreateDto){
        Order order = new Order();
        BeanUtils.copyProperties(orderCreateDto, order);

        List<OrderProduct> orderProducts = orderCreateDto.getOrderProductList().stream()
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
    Order orderPatchDtoToOrder(OrderUpdateDto orderUpdateDto);

    OrderResponseDto orderToOrderResponseDto (Order order);

    default OrderDetailResponseDto orderToOrderDetailResponse(Order order){

        OrderDetailResponseDto orderDetailResponseDto = new OrderDetailResponseDto();

        BeanUtils.copyProperties(order,orderDetailResponseDto);

        List<OrderProductDetailResponseDto> orderProductDtos = order.getOrderProductList().stream()
                .map(orderProduct -> {
                    OrderProductDetailResponseDto orderProductDetailResponseDto = new OrderProductDetailResponseDto();
                    BeanUtils.copyProperties(orderProduct, orderProductDetailResponseDto);
                    orderProductDetailResponseDto.setProductResponse(ProductMapper.productToProductResponseDto(orderProduct.getProduct()));
                    return orderProductDetailResponseDto;
                })
                .collect(Collectors.toList());

        orderDetailResponseDto.setOrderProductList(orderProductDtos);

        return orderDetailResponseDto;
    }

    List<OrderResponseDto> ordersToOrderResponseDtos(List<Order> orderList);






}
