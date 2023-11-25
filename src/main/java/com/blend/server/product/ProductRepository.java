package com.blend.server.product;

import com.blend.server.category.Category;
import com.blend.server.seller.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByProductStatusIn(Set<Product.ProductStatus> statuses, Pageable pageable);

    Page<Product> findAllBySellerAndProductStatus(Seller seller, Product.ProductStatus productStatus, Pageable pageable);

    Optional<Product> findByIdAndSeller(Long id, Seller seller);



}

