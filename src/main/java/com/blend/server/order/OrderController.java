package com.blend.server.order;

import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createOrder (@RequestBody OrderCreateDto orderCreateDto){

        Order order = service.createOrder(mapper.orderPostDtoToOrder(orderCreateDto));

        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, order.getId());

        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "주문 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity updateOrder (@PathVariable long id,
                                       @RequestBody OrderUpdateDto orderUpdateDto){
        orderUpdateDto.setId(id);
        Order order = service.updateOrder(id,mapper.orderPatchDtoToOrder(orderUpdateDto));

        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    @ApiOperation(value = "주문 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity findOrder(@PathVariable long id) {
        Order order = service.findOrder(id);
        return new ResponseEntity<>(mapper.orderToOrderDetailResponse(order), HttpStatus.OK);
    }

    @ApiOperation(value = "주문 취소 API")
    @PatchMapping("cancel/{id}")
    public ResponseEntity cancelOrder(@PathVariable long id){
        Order order = service.cancelOrder(id);
        return new ResponseEntity<>(mapper.orderToOrderDetailResponse(order),HttpStatus.OK);
    }

    // 회원 추가 후 판매자, 사용자로 전체 주문 조회 분리 ( 해당 로직 사용자)
    @ApiOperation(value = "전체 주문 조회 API")
    @GetMapping
    public ResponseEntity findAllOrder(@RequestParam int page,
                                       @RequestParam int size){
        Page<Order> orderPage = service.findAllOrder(page-1, size);
        List<Order> orderList = orderPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.ordersToOrderResponseDtos(orderList),orderPage), HttpStatus.OK);
    }

}
