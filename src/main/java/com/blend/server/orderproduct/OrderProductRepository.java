package com.blend.server.orderproduct;

import com.blend.server.seller.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {

    Page<OrderProduct> findByProductSeller(Seller seller, Pageable pageable);
}
