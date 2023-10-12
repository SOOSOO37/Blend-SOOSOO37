package com.blend.server.seller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {

    Optional<Seller> findByRegNumber(String regNumber);

    Optional<Seller> findByUser(User user);
}
