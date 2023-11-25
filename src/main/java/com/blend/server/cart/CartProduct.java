package com.blend.server.cart;

import com.blend.server.product.Product;
import com.blend.server.global.audit.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class CartProduct extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartProductId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int productCount = 1;

    @Builder
    public CartProduct(Long cartProductId, Cart cart, Product product) {
        this.cartProductId = cartProductId;
        this.cart = cart;
        this.product = product;
        this.addCart(cart);
    }
    public void addCart(Cart cart){
        this.cart = cart;
        if(!cart.getCartProductList().contains(this)){
            cart.getCartProductList().add(this);
        }
    }
}
