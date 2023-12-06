package com.blend.server.test;

import com.blend.server.cart.*;
import com.blend.server.category.CategoryRepository;
import com.blend.server.category.CategoryService;
import com.blend.server.product.Product;
import com.blend.server.product.ProductService;
import com.blend.server.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("장바구니 상품 추가 테스트")
    @Test
    public void addToCartTest() {
        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);

        CartProduct savedCartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .cartProductId(1L)
                .build();

        when(cartProductRepository.findByProductIdAndCartId(product.getId(), cart.getId())).thenReturn(Optional.empty());
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productService.findProduct(product.getId())).thenReturn(product);
        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(savedCartProduct);

        CartProduct result = cartService.addToCart(cart.getId(), product.getId());


        assertNotNull(result);
        assertEquals(cart, result.getCart());
        assertEquals(product, result.getProduct());
        assertEquals(cart.getCartProductList().get(0).getProduct(),product);

        }

    @DisplayName("장바구니 전체 상품 조회")
    @Test
    public void findCartProductsTest() {

        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setCart(cart);

        Product product = new Product();
        product.setId(1L);

        CartProduct savedCartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .cartProductId(1L)
                .build();

        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(savedCartProduct);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartProductRepository.findByCart(PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                cart)).thenReturn(new PageImpl<>(cartProducts));

        Page<CartProduct> result = cartService.findCartProducts(0, 10, user);

        assertNotNull(result);
        assertEquals(savedCartProduct.getProduct(), result.getContent().get(0).getProduct());
        assertEquals(user.getCart(),result.getContent().get(0).getCart());
    }

    @DisplayName("장바구니 상품 하나 삭제 테스트")
    @Test
    public void deleteCartProduct_Success() {

        Product product = new Product();
        product.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);

        CartProduct savedCartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .cartProductId(1L)
                .build();

        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(savedCartProduct);

        cartService.deleteCartProduct(product.getId(), cart.getId());

        verify(cartProductRepository, times(1)).deleteByProductIdAndCartId(product.getId(), cart.getId());
    }

    @DisplayName("장바구니 상품 전체 삭제")
    @Test
    public void deleteCartProductsByUser() {

        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);

        CartProduct savedCartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .cartProductId(1L)
                .build();

        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(savedCartProduct);

        cartService.deleteCartProductsByUser(cart.getId());

        verify(cartProductRepository, times(1)).deleteAllByCartId(cart.getId());

    }

    @DisplayName("장바구니 상품 수량 증가 테스트")
    @Test
    public void addCountTest(){

        Product product = new Product();
        product.setId(1L);
        product.setProductCount(10);

        Cart cart = new Cart();
        cart.setId(1L);

        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setProductCount(2);

        when(cartProductRepository.findByProductIdAndCartId(product.getId(), cart.getId())).thenReturn(Optional.of(cartProduct));

        cartService.addCount(product.getId(), cart.getId());

        assertEquals(3, cartProduct.getProductCount());
    }

    @DisplayName("장바구니 상품 수량 감소 테스트")
    @Test
    public void reduceCountTest(){

        Product product = new Product();
        product.setId(1L);
        product.setProductCount(10);

        Cart cart = new Cart();
        cart.setId(1L);

        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setProductCount(2);

        when(cartProductRepository.findByProductIdAndCartId(product.getId(), cart.getId())).thenReturn(Optional.of(cartProduct));

        cartService.reduceCount(product.getId(), cart.getId());

        assertEquals(1, cartProduct.getProductCount());
    }




}
