package com.blend.server.cart;

import com.blend.server.Product.Product;
import com.blend.server.global.audit.Auditable;
import com.blend.server.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Cart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "cart")
    private List<CartProduct> cartProductList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Cart(long id, int totalPrice, List<CartProduct> cartProductList) {
        this.id = id;
        this.cartProductList = cartProductList;
    }
}
