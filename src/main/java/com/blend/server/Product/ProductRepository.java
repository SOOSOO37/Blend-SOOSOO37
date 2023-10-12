package com.blend.server.Product;

import com.blend.server.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByProductStatus(Product.ProductStatus status, Pageable pageable);

    Page<Product> findAllBySellerIdAndProductStatusNotLike(Long id, Product.ProductStatus productStatus, Pageable pageable);
}
