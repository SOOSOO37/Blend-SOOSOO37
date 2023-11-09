package com.blend.server.seller;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductDetailResponseDto;
import com.blend.server.Product.ProductMapper;
import com.blend.server.Product.ProductResponseDto;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.order.Order;
import com.blend.server.user.User;
import com.blend.server.user.UserResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping
    private ResponseEntity createSeller(@Valid @RequestBody SellerPostDto sellerPostDto){

        Seller seller = service.createSeller(mapper.sellerPostDtoToSeller(sellerPostDto));

        URI location = UriCreator.createUri(SELLER_DEFAULT_URL, seller.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping
    public ResponseEntity updateSeller(@RequestBody SellerPatchDto sellerPatchDto,
                                       @AuthenticationPrincipal Seller seller){

        sellerPatchDto.setId(seller.getId());
        Seller findSeller = service.updateSeller(mapper.sellerPatchDtoToSeller(sellerPatchDto));

        return new ResponseEntity<>(findSeller, HttpStatus.OK);
    }

    @GetMapping("/sale-product")
    public ResponseEntity findSaleProducts(@RequestParam int page,
                                           @RequestParam int size,
                                           @AuthenticationPrincipal Seller seller){
        //Seller findSeller = service.findVerifiedSeller(seller.getId());
        Page<Product> productPage = service.findProducts(size, page, seller);
        List<Product> productList = productPage.getContent();
        List<ProductResponseDto> response = productMapper.productsToProductResponseDtos(productList);

        return new ResponseEntity<>(new MultiResponseDto<>(response, productPage), HttpStatus.OK);
    }

    @DeleteMapping("/{product-id}")
    public ResponseEntity deleteSaleProduct(@AuthenticationPrincipal Seller seller,
                                            @PathVariable("product-id") long productId){
        service.deleteProduct(productId,seller);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
