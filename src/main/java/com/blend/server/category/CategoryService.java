package com.blend.server.category;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category){
        log.info("Creating Category - Name: {}", category.getName());
        if (isCategoryNameExists(category.getName())) {
            log.warn("Category name already exists: {}", category.getName());
            throw new BusinessLogicException(ExceptionCode.CATEGORY_EXISTS);
        }
        Category savedCategory = categoryRepository.save(category);
        log.info("Created Category - ID: {}, Name: {}", savedCategory.getId(), savedCategory.getName());

        return savedCategory;
    }

    public Page<Category> findAllCategory(int page, int size){
        log.info("Finding All Categories - Page: {}, Size: {}", page, size);
        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        log.info("Categories retrieved successfully - Page: {}, Size: {}, Total Categories: {}",
                page, size, categoryPage.getTotalElements());

        return categoryPage;
    }

    public Category findCategory (String name){
        log.info("Finding Category Name - Name: {}", name);
        return categoryRepository.findByName(name);
    }

    private boolean isCategoryNameExists(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }
}
