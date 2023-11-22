package com.blend.server.cart;

import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/carts")
@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;

    private final ProductService productService;

    private final static String CART_DEFAULT_URL = "/carts";
    private final CartMapper cartMapper;

    //장바구니 상품 등록
    @PostMapping("/{product-id}")
    public ResponseEntity addProducts(@PathVariable("product-id")long productId,
                                      @AuthenticationPrincipal User user){

        Product product = productService.findVerifiedProduct(productId);
        Cart cart = cartService.findVerifiedCartByUser(user);
        cartService.addToCart(cart.getId(),product.getId());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    // 장바구니 조회
    @GetMapping("/my-cart")
    public ResponseEntity findAllCartProducts(@AuthenticationPrincipal User user,
                                              @RequestParam int page,
                                              @RequestParam int size) {

        Cart cart = cartService.findVerifiedCartByUser(user);
        Page<CartProduct> cartProductPage = cartService.findCartProducts(page - 1, size, user);
        List<CartProduct> cartProductList = cartProductPage.getContent();
        CartResponseDto cartResponseDto = cartMapper.cartProductsToCartResponseDto(cartProductList, cart);

        return new ResponseEntity<>(cartResponseDto, HttpStatus.OK);
    }

    // 장바구니 상품 수량 올리기
    @PatchMapping("/add/{product-id}")
    public ResponseEntity addProductCount(@PathVariable("product-id")long productId,
                                          @AuthenticationPrincipal User user){
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.addCount(productId,cartId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 장바구니 상품 수량 줄이기
    @PatchMapping("/reduce/{product-id}")
    public ResponseEntity reduceProductCount(@PathVariable("product-id")long productId,
                                             @AuthenticationPrincipal User user){
        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.reduceCount(productId,cartId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    // 장바구니 상품 하나씩 삭제
    @DeleteMapping("/{product-id}")
    public ResponseEntity deleteCartProduct(@PathVariable("product-id") long productId,
                                            @AuthenticationPrincipal User user){

        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.deleteCartProduct(productId, cartId);

        return new ResponseEntity(HttpStatus.OK);
    }

    //장바구니 전체 상품 삭제
    @DeleteMapping
    public ResponseEntity deleteAllCartProduct(@AuthenticationPrincipal User user) {

        Cart cart = cartService.findVerifiedCartByUser(user);
        long cartId = cart.getId();
        cartService.deleteCartProductsByUser(cartId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
