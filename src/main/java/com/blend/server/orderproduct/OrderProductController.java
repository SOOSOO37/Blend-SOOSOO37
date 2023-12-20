package com.blend.server.orderproduct;

import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.seller.Seller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "OrderProduct API Controller")
@Slf4j
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
                                               @RequestParam int size,
                                               @AuthenticationPrincipal Seller seller){

        log.info("Inquiring OrderProduct");
        Page<OrderProduct> orderProductPage = orderProductService.findAllOrderProductBySeller(page -1,size,seller);
        List<OrderProduct> orderProductList = orderProductPage.getContent();
        log.info("Found OrderProduct for seller : {}", seller.getId());
        return new ResponseEntity<>(new MultiResponseDto<>
                (mapper.orderProductListToOrderProcutSellerResponseDtos(orderProductList),orderProductPage), HttpStatus.OK);
    }

    // 주문 상품 상태 변경 및 운송 번호 등록
    @ApiOperation(value = "주문상품 상태 변경 및 운송 번호 등경 API")
    @PatchMapping("/{orderProduct-id}")
    public ResponseEntity updateOrderStatus(@PathVariable("orderProduct-id") long orderProductId,
                                            @RequestBody OrderProductUpdateDto orderProductUpdateDto,
                                            @AuthenticationPrincipal Seller seller){
        log.info("---Updating OrderProduct---");
        OrderProduct orderProduct = orderProductService.updateOrderStatus(orderProductId, orderProductUpdateDto,seller);
        OrderProductSellerResponseDto response = mapper.orderProductToOrderSellerResponseDto(orderProduct);
        log.info("Updated  OrderProduct : {}, Seller : {}", orderProductId, seller.getId());

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
