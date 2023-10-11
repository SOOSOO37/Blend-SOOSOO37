package com.blend.server.orderproduct;

import com.blend.server.global.response.MultiResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "OrderProduct API Controller")
@RequestMapping("/orderProducts")
@RequiredArgsConstructor
@RestController
public class OrderProductController {

    private final OrderProductService orderProductService;

    private final OrderProductRepository orderProductRepository;

    private final OrderProductMapper mapper;

    // 주문 상품 조회 (판매자)
    @ApiOperation(value = "전체 주문상품 조회 API")
    @GetMapping("/saleList")
    public ResponseEntity getOrderProductList (@RequestParam int page,
                                        @RequestParam int size){

        Page<OrderProduct> orderProductPage = orderProductService.findAllOrderProduct(page -1,size);
        List<OrderProduct> orderProductList = orderProductPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>
                (mapper.orderProductListToOrderProcutSellerResponseDtos(orderProductList),orderProductPage), HttpStatus.OK);
    }

    // 주문 상품 상태 변경 및 운송 번호 등록
    @ApiOperation(value = "주문상품 상태 변경 및 운송 번호 등경 API")
    @PatchMapping("/{orderProduct-id}")
    public ResponseEntity updateOrderStatus(@PathVariable("orderProduct-id") long orderProductId,
                                            @RequestBody OrderProductUpdateDto orderProductUpdateDto){
        OrderProduct orderProduct = orderProductService.updateOrderStatus(orderProductId, orderProductUpdateDto);
        OrderProductSellerResponseDto response = mapper.orderProductToOrderSellerResponseDto(orderProduct);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
