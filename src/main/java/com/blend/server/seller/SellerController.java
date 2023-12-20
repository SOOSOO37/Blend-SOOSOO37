package com.blend.server.seller;

import com.blend.server.product.Product;
import com.blend.server.product.ProductMapper;
import com.blend.server.product.ProductResponseDto;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.order.OrderMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Api(tags = "Seller API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sellers")
@RestController
public class SellerController {

    @Value("${config.domain}")
    private String domain;

    private final static String SELLER_DEFAULT_URL = "/sellers";
    private final SellerService service;
    private final SellerMapper mapper;
    private final ProductMapper productMapper;

    private final OrderMapper orderMapper;

    @ApiOperation(value = "판매자 회원가입 API")
    @PostMapping
    private ResponseEntity createSeller(@Valid @RequestBody SellerPostDto sellerPostDto){
        log.info("---Sign Up Seller---");
        Seller seller = service.createSeller(mapper.sellerPostDtoToSeller(sellerPostDto));
        log.info("Seller created: {}", seller);
        URI location = UriCreator.createUri(SELLER_DEFAULT_URL, seller.getId());
        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "판매자 정보수정 API")
    @PatchMapping
    public ResponseEntity updateSeller(@RequestBody SellerPatchDto sellerPatchDto,
                                       @AuthenticationPrincipal Seller seller){
        log.info("---Updating Seller---");
        sellerPatchDto.setId(seller.getId());
        Seller findSeller = service.updateSeller(mapper.sellerPatchDtoToSeller(sellerPatchDto));
        log.info("Updated Seller :{}", findSeller.getId());
        return new ResponseEntity<>(findSeller, HttpStatus.OK);
    }

    @ApiOperation(value = "해당 판매자가 판매중인 상품 조회 API")
    @GetMapping("/sale-product")
    public ResponseEntity findSaleProducts(@RequestParam int page,
                                           @RequestParam int size,
                                           @AuthenticationPrincipal Seller seller){
        log.info("---Finding Sale Products---");
        Page<Product> productPage = service.findProducts(size, page, seller);
        List<Product> productList = productPage.getContent();
        List<ProductResponseDto> response = productMapper.productsToProductResponseDtos(productList);
        log.info("Sale products Found for seller with ID: {}", seller.getId());
        return new ResponseEntity<>(new MultiResponseDto<>(response, productPage), HttpStatus.OK);
    }
    @ApiOperation(value = "판매자가 등록한 상품 삭제 API")
    @DeleteMapping("/{product-id}")
    public ResponseEntity deleteSaleProduct(@AuthenticationPrincipal Seller seller,
                                            @PathVariable("product-id") long productId){
        log.info("---Deleting Sale Product---");
        service.deleteProduct(productId,seller);
        log.info("Sale product deleted : {}", productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
