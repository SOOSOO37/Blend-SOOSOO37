package com.blend.server.order;

import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

@Api(tags = "Order API Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final static String ORDER_DEFAULT_URL = "/orders";

    private final OrderMapper mapper;

    private final OrderService service;

    @ApiOperation(value = "주문 생성 API")
    @PostMapping
    public ResponseEntity createOrder (@RequestBody OrderCreateDto orderCreateDto,
                                       @AuthenticationPrincipal User user){

        orderCreateDto.setUserId(user.getId());
        Order order = service.createOrder(mapper.orderPostDtoToOrder(orderCreateDto),user);

        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, order.getId());

        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "주문 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity updateOrder (@PathVariable long id,
                                       @RequestBody OrderUpdateDto orderUpdateDto,
                                       @AuthenticationPrincipal User user){
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        orderUpdateDto.setId(id);
        Order order = service.updateOrder(user,mapper.orderPatchDtoToOrder(orderUpdateDto));
        OrderResponseDto response = mapper.orderToOrderResponseDto(order);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @ApiOperation(value = "주문 상품 수량 업데이트 API")
    @PatchMapping("/{orderId}/{orderProductId}")
    public ResponseEntity updateOrderProductQuantity(@AuthenticationPrincipal User user,
                                                            @PathVariable long orderId,
                                                            @PathVariable long orderProductId,
                                                            @RequestBody OrderCountUpdateDto orderCountUpdateDto) {

            Order updatedOrder = service.updateQuantity(user, orderId, orderProductId, orderCountUpdateDto.getNewQuantity());
            OrderDetailResponseDto response = mapper.orderToOrderDetailResponse(updatedOrder);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "주문 상세조회 API")
    @GetMapping("/{id}")
    public ResponseEntity findOrder(@PathVariable long id,
                                    @AuthenticationPrincipal User user) {
        Order order = service.findOrder(user,id);
        return new ResponseEntity<>(mapper.orderToOrderDetailResponse(order), HttpStatus.OK);
    }

    @ApiOperation(value = "주문 취소 API")
    @PatchMapping("cancel/{id}")
    public ResponseEntity cancelOrder(@PathVariable long id,
                                      @AuthenticationPrincipal User user){
        Order order = service.cancelOrder(user,id);
        return new ResponseEntity<>(mapper.orderToOrderDetailResponse(order),HttpStatus.OK);
    }

    // 회원
    @ApiOperation(value = "전체 주문 조회 API")
    @GetMapping
    public ResponseEntity findAllOrder(@RequestParam int page,
                                       @RequestParam int size,
                                       @AuthenticationPrincipal User user){
        Page<Order> orderPage = service.findAllOrder(page-1, size,user);
        List<Order> orderList = orderPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.ordersToOrderResponseDtos(orderList),orderPage), HttpStatus.OK);
    }

}
