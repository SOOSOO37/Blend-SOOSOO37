package com.blend.server.answer;


import com.blend.server.seller.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByIdAndSeller (Long id, Seller seller);

    Page<Answer> findAllBySeller (Seller seller, Pageable pageable);
}
