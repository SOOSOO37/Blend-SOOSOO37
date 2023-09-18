package com.blend.server.Product;

import com.blend.server.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByProductStatus(Product.ProductStatus status, Pageable pageable);
}
