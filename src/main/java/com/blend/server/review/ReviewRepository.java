package com.blend.server.review;

import com.blend.server.product.Product;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    boolean existsByUserAndProduct(User user, Product product);

    Optional<Review> findByIdAndUser(Long id, User user);

    Page<Review> findAllByUserAndReviewStatus(User user, Review.ReviewStatus reviewStatus, Pageable pageable);

    Page<Review> findAllByProductIdAndReviewStatus(long productId, Review.ReviewStatus reviewStatus, Pageable pageable);

}
