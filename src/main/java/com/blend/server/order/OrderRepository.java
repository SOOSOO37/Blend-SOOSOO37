package com.blend.server.order;

import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findByUser(User user, Pageable pageable);

}
