package com.blend.server.cart;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductService;
import com.blend.server.category.Category;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final ProductService productService;

    // 멤버추가 유저 아이디당 1개 생성 (임시로직)
    public Cart createCart(Cart cart) {

        return cartRepository.save(cart);
    }

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

    //유효한 장바구니와 상품인지 검증
    private CartProduct findVerifiedCartProduct(Long cartId, Long productId) {
        CartProduct cartProduct = cartProductRepository.findByProductIdAndCartId(productId,cartId)
                .orElseThrow(()->{
                    throw new RuntimeException("cartProduct 에러");
                });
        return cartProduct;
    }

    private void verifiedCartProduct(Long cartId, Long productId){
        Optional<CartProduct> optionalCartProduct = cartProductRepository.findByProductIdAndCartId(productId, cartId);
        optionalCartProduct.ifPresent(cartProduct -> {
            throw new RuntimeException("이미 들어간 상품입니다.");
        });
    }


    //장바구니가 있는지 검증
    public Cart findVerifiedCart(long id) {
        Optional<Cart> cart = cartRepository.findById(id);

        Cart findCart = cart.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        return findCart;

    }

//    // 회원별로 조회로 변경 예정
//    public Page<CartProduct> findCartProducts(int page, int size, long cartId) {
//
//        Cart cart = findVerifiedCart(cartId);
//
//        return cartProductRepository.findByCartId(PageRequest.of(page, size, Sort.by("createdAt").descending()), cart);
//    }

    public Page<CartProduct> findCartProducts(int page, int size, long cartId) {
        // cartId를 사용하여 Cart 객체를 검색
        Cart cart = findVerifiedCart(cartId);

        // PageRequest를 사용하여 페이지 및 사이즈 설정
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Cart 객체를 사용하여 CartProduct를 검색
        return cartProductRepository.findByCart(pageRequest,cart);
    }


    public void deleteCartProduct(long productId, long cartId){
        cartProductRepository.deleteByProductIdAndCartId(productId,cartId);
    }

    public void deleteCartProductsByCartId(Long cartId){
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
        if(findCartProduct.getProductCount() == 0){
            throw new RuntimeException("최소 수량은 1개 입니다.");
        }else {
            findCartProduct.setProductCount(findCartProduct.getProductCount() -1);
        }
        cartProductRepository.save(findCartProduct);
    }


}
