package com.blend.server.cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByProductIdAndCartId(Long productId, Long cartId);

    Page<CartProduct> findByCart(Pageable pageable, Cart cart);

    void deleteByProductIdAndCartId(Long productId, Long cartId);

    void deleteAllByCartId(long cartId);
}
