package com.blend.server.cart;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    @PostMapping("/{cart-id}/{product-id}")
    public ResponseEntity addProducts(@PathVariable("product-id")long productId,
                                      @PathVariable ("cart-id")long cartId){

        Product product = productService.findVerifiedProduct(productId);
        Cart cart = cartService.findVerifiedCart(cartId);
        cartService.addToCart(cart.getId(),product.getId());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    // 장바구니 조회
    @GetMapping("{cart-id}")
    public ResponseEntity findAllCartProducts(@PathVariable ("cart-id")long cartId, int page, int size){

        Page<CartProduct> cartProductPage = cartService.findCartProducts(page -1, size, cartId);
        List<CartProduct> cartProductList = cartProductPage.getContent();
        CartResponseDto cartResponseDto = cartMapper.cartProductsToCartResponseDto(cartProductList, cartId);

        return new ResponseEntity<>(cartResponseDto,HttpStatus.OK);
    }

    // 장바구니 상품 수량 올리기
    @PatchMapping("/add//{cart-id}/{product-id}")
    public ResponseEntity addProductCount(@PathVariable("product-id")long productId,
                                          @PathVariable("cart-id") long cartId){
        cartService.addCount(productId,cartId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 장바구니 상품 수량 줄이기
    @PatchMapping("/reduce//{cart-id}/{product-id}")
    public ResponseEntity reduceProductCount(@PathVariable("product-id")long productId,
                                          @PathVariable("cart-id") long cartId){
        cartService.reduceCount(productId,cartId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    // 장바구니 상품 하나씩 삭제
    @DeleteMapping("/{cart-id}/{product-id}")
    public ResponseEntity deleteCartProduct(@PathVariable("product-id") long productId,
                                         @PathVariable("cart-id") long cartId){

        cartService.deleteCartProduct(productId, cartId);

        return new ResponseEntity(HttpStatus.OK);
    }

    //장바구니 전체 상품 삭제
    @DeleteMapping("/{cart-id}")
    public ResponseEntity deleteAllCartProduct(@PathVariable("cart-id") long cartId) {

        cartService.deleteCartProductsByCartId(cartId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
