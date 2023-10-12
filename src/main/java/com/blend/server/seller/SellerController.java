package com.blend.server.seller;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductMapper;
import com.blend.server.Product.ProductResponseDto;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.order.Order;
import com.blend.server.user.User;
import com.blend.server.user.UserResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/sellers")
@RestController
public class SellerController {

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

    @PatchMapping("/{id}")
    public ResponseEntity updateSeller(@PathVariable long id,
                                       @RequestBody SellerPatchDto sellerPatchDto){
        sellerPatchDto.setId(id);
        Seller seller = service.updateSeller(mapper.sellerPatchDtoToSeller(sellerPatchDto));

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findSeller(@PathVariable long id) {
        Seller findSeller = service.findSeller(id);
        SellerResponseDto response = mapper.sellerToSellerResponseDto(findSeller);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/sale-product/{id}")
    public ResponseEntity findSaleProducts(@RequestParam int page,
                                           @RequestParam int size,
                                           @PathVariable long id){

        Page<Product> productPage = service.findProducts(page-1,size,id);
        List<Product> productList = productPage.getContent();
        List<ProductResponseDto> response = productMapper.productsToProductResponseDtos(productList);

        return new ResponseEntity<>(new MultiResponseDto<>(response,productPage), HttpStatus.OK);
    }

    @DeleteMapping("/{seller-id}/{product-id}")
    public ResponseEntity deleteSaleProduct(@PathVariable("seller-id") long sellerId,
                                            @PathVariable("product-id") long productId){
        service.deleteProduct(sellerId,productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
