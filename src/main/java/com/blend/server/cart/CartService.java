package com.blend.server.cart;

import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final ProductService productService;


    // 장바구니에 상품 담기
    public CartProduct addToCart(long cartId, long productId) {
        log.info("Adding Product - CartId: {}, ProductId: {}", cartId, productId);
        verifiedCartProduct(cartId, productId);
        Cart cart = findVerifiedCart(cartId);
        Product product = productService.findProduct(productId);
        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .build();

        CartProduct savedCartProduct = cartProductRepository.save(cartProduct);
        log.info("Added Product - CartId: {}, ProductId: {}", cartId, productId);

        return savedCartProduct;
    }

    private CartProduct findVerifiedCartProduct(Long cartId, Long productId) {
        log.info("Finding cart product - CartId: {}, ProductId: {}", cartId, productId);
        CartProduct cartProduct = cartProductRepository.findByProductIdAndCartId(productId,cartId)
                .orElseThrow(()->{
                    log.warn("Verified CartProduct not found - CartId: {}, ProductId: {}", cartId, productId);
                    throw new BusinessLogicException(ExceptionCode.CARTPRODUCT_NOT_FOUND);
                });
        log.info("Verified CartProduct - CartId: {}, ProductId: {}", cartId, productId);
        return cartProduct;
    }

    private void verifiedCartProduct(Long cartId, Long productId){
        log.info("Verifying CartProduct - CartId: {}, ProductId: {}", cartId, productId);
        Optional<CartProduct> optionalCartProduct = cartProductRepository.findByProductIdAndCartId(productId, cartId);
        optionalCartProduct.ifPresent(cartProduct -> {
            throw new BusinessLogicException(ExceptionCode.CARTPRODUCT_EXISTS);
        });
        log.info("Verified Cart product - CartId: {}, ProductId: {}", cartId, productId);
    }

    //장바구니가 있는지 검증
    public Cart findVerifiedCart(long id) {
        log.info("Verified Cart - CartId: {}", id);
        Optional<Cart> cart = cartRepository.findById(id);
        Cart findCart = cart.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        log.info("Verified Cart - CartId: {}", id);
        return findCart;
    }

    public Cart findVerifiedCartByUser(User user){
        log.info("Verified Cart by user - User: {}", user.getId());
        Cart findCart = cartRepository.findByUser(user)
                .orElseThrow(()->{
                    log.warn("Cart not found - User: {}", user.getId());
                    throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
                });
        log.info("Verified Cart - User: {}, CartId: {}", user.getId(), findCart.getId());
        return findCart;
    }

    public Page<CartProduct> findCartProducts(int page, int size, User user) {
        log.info("Finding cart products - User: {}, Page: {}, Size: {}", user.getId(), page, size);
        Cart cart = findVerifiedCartByUser(user);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CartProduct> cartProductPage = cartProductRepository.findByCart(pageRequest, cart);
        log.info("Found Cart products - User: {}, Page: {}, Size: {}, Total Products: {}",
                user.getId(), page, size, cartProductPage.getTotalElements());

        return cartProductPage;
    }


    public void deleteCartProduct(long productId, long cartId){
        log.info("Deleting CartProduct - ProductId: {}, CartId: {}", productId, cartId);
        cartProductRepository.deleteByProductIdAndCartId(productId,cartId);
        log.info("Deleted CartProduct - ProductId: {}, CartId: {}", productId, cartId);
    }

    public void deleteCartProductsByUser(Long cartId){
        log.info("Deleting All CartProducts by user - CartId: {}", cartId);
        cartProductRepository.deleteAllByCartId(cartId);
        log.info("Deleted All CartProduct - CartId: {}", cartId);
    }

    public void addCount(long productId, long cartId){
        log.info("Increasing ProductCount - ProductId: {}, CartId: {}", productId, cartId);
        CartProduct findCartProduct = findVerifiedCartProduct(cartId,productId);
        if(findCartProduct.getProductCount()==(findCartProduct.getProduct().getProductCount())){
            log.warn("Product count cannot be increased - ProductId: {}, CartId: {}", productId, cartId);
            throw new RuntimeException("상품 재고가 없습니다.");
        }else{
            findCartProduct.setProductCount(findCartProduct.getProductCount()+1);
        }
        cartProductRepository.save(findCartProduct);
    }

    public void reduceCount(long productId, long cartId){
        log.info("Reducing ProductCount - ProductId: {}, CartId: {}", productId, cartId);
        CartProduct findCartProduct = findVerifiedCartProduct(cartId,productId);
        if(findCartProduct.getProductCount() < 1){
            log.warn("Product count cannot be reduced below 1 - ProductId: {}, CartId: {}", productId, cartId);
            throw new RuntimeException("최소 수량은 1개 입니다.");
        }else {
            findCartProduct.setProductCount(findCartProduct.getProductCount() -1);
        }
        cartProductRepository.save(findCartProduct);
    }

}
