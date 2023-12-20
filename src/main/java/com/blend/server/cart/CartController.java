package com.blend.server.cart;

import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.user.User;
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

@Api(tags = "Cart API Controller")
@Slf4j
@RequestMapping("/carts")
@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;

    private final ProductService productService;

    private final static String CART_DEFAULT_URL = "/carts";
    private final CartMapper cartMapper;

    @ApiOperation(value = "장바구니 상품 등록 API")
    @PostMapping("/{product-id}")
    public ResponseEntity addProducts(@PathVariable("product-id")long productId,
                                      @AuthenticationPrincipal User user){
        log.info("Adding Product - ProductId: {}, User: {}", productId, user.getId());
        Product product = productService.findVerifiedProduct(productId);
        Cart cart = cartService.findVerifiedCartByUser(user);
        cartService.addToCart(cart.getId(),product.getId());
        log.info("Added Product - ProductId: {}, User: {}", productId, user.getId());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "장바구니 조회 API")
    @GetMapping("/my-cart")
    public ResponseEntity findAllCartProducts(@AuthenticationPrincipal User user,
                                              @RequestParam int page,
                                              @RequestParam int size) {
        log.info("Finding Carts - User: {}, Page: {}, Size: {}", user.getId(), page, size);
        Cart cart = cartService.findVerifiedCartByUser(user);
        Page<CartProduct> cartProductPage = cartService.findCartProducts(page - 1, size, user);
        List<CartProduct> cartProductList = cartProductPage.getContent();
        CartResponseDto cartResponseDto = cartMapper.cartProductsToCartResponseDto(cartProductList, cart);
        log.info("Found Cart products - User: {}, Page: {}, Size: {}, Total Products: {}",
                user.getId(), page, size, cartProductPage.getTotalElements());

        return new ResponseEntity<>(cartResponseDto, HttpStatus.OK);
    }

    @ApiOperation(value = "장바구니 수량 증가 API")
    @PatchMapping("/add/{product-id}")
    public ResponseEntity addProductCount(@PathVariable("product-id")long productId,
                                          @AuthenticationPrincipal User user){
        log.info("Increasing Product Count - ProductId: {}, User: {}", productId, user.getId());
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.addCount(productId,cartId);
        log.info("Product count increased - ProductId: {}, User: {}", productId, user.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "장바구니 수량 감소 API")
    @PatchMapping("/reduce/{product-id}")
    public ResponseEntity reduceProductCount(@PathVariable("product-id")long productId,
                                             @AuthenticationPrincipal User user){
        log.info("Reducing Product Count - ProductId: {}, User: {}", productId, user.getId());
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.reduceCount(productId,cartId);
        log.info("Product count reduced - ProductId: {}, User: {}", productId, user.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "장바구니 상품 한개 삭제 API")
    @DeleteMapping("/{product-id}")
    public ResponseEntity deleteCartProduct(@PathVariable("product-id") long productId,
                                            @AuthenticationPrincipal User user){
        log.info("Deleting product from cart - ProductId: {}, User: {}", productId, user.getId());
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.deleteCartProduct(productId, cartId);
        log.info("Deleted Product - ProductId: {}, User: {}", productId, user.getId());

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "장바구니 상품 전체 삭제 API")
    @DeleteMapping
    public ResponseEntity deleteAllCartProduct(@AuthenticationPrincipal User user) {
        log.info("Deleting all cart products - User: {}", user.getId());
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.deleteCartProductsByUser(cartId);
        log.info("Deleted All cartProducts  - User: {}", user.getId());

        return new ResponseEntity(HttpStatus.OK);
    }
}
