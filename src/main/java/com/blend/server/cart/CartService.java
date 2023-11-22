package com.blend.server.cart;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.Order;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final ProductService productService;


    // 장바구니에 상품 담기
    public CartProduct addToCart(long cartId, long productId) {
        verifiedCartProduct(cartId, productId);
        Cart cart = findVerifiedCart(cartId);
        Product product = productService.findProduct(productId);
        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .build();

        return cartProductRepository.save(cartProduct);
    }

    private CartProduct findVerifiedCartProduct(Long cartId, Long productId) {
        CartProduct cartProduct = cartProductRepository.findByProductIdAndCartId(productId,cartId)
                .orElseThrow(()->{
                    throw new BusinessLogicException(ExceptionCode.CARTPRODUCT_NOT_FOUND);
                });
        return cartProduct;
    }

    private void verifiedCartProduct(Long cartId, Long productId){
        Optional<CartProduct> optionalCartProduct = cartProductRepository.findByProductIdAndCartId(productId, cartId);
        optionalCartProduct.ifPresent(cartProduct -> {
            throw new BusinessLogicException(ExceptionCode.CARTPRODUCT_EXISTS);
        });
    }


    //장바구니가 있는지 검증
    public Cart findVerifiedCart(long id) {
        Optional<Cart> cart = cartRepository.findById(id);

        Cart findCart = cart.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        return findCart;

    }

    public Cart findVerifiedCartByUser(User user){
        Cart findCart = cartRepository.findByUser(user)
                .orElseThrow(()->{
                    throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
                });
        return findCart;
    }

    public Page<CartProduct> findCartProducts(int page, int size, User user) {

        Cart cart = findVerifiedCartByUser(user);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return cartProductRepository.findByCart(pageRequest,cart);
    }


    public void deleteCartProduct(long productId, long cartId){
        cartProductRepository.deleteByProductIdAndCartId(productId,cartId);
    }

    public void deleteCartProductsByUser(Long cartId){
        cartProductRepository.deleteAllByCartId(cartId);
    }

    public void addCount(long productId, long cartId){
        CartProduct findCartProduct = findVerifiedCartProduct(cartId,productId);
        if(findCartProduct.getProductCount()==(findCartProduct.getProduct().getProductCount())){
            throw new RuntimeException("상품 재고가 없습니다.");
        }else{
            findCartProduct.setProductCount(findCartProduct.getProductCount()+1);
        }
        cartProductRepository.save(findCartProduct);
    }

    public void reduceCount(long productId, long cartId){
        CartProduct findCartProduct = findVerifiedCartProduct(cartId,productId);
        if(findCartProduct.getProductCount() < 1){
            throw new RuntimeException("최소 수량은 1개 입니다.");
        }else {
            findCartProduct.setProductCount(findCartProduct.getProductCount() -1);
        }
        cartProductRepository.save(findCartProduct);
    }

}
