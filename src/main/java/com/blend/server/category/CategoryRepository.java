package com.blend.server.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByIdAndName(long id, String name);

    Category findByName(String name);

}
