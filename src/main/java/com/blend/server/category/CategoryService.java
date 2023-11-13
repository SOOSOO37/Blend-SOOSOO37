package com.blend.server.category;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category){
        if (isCategoryNameExists(category.getName())) {
            throw new BusinessLogicException(ExceptionCode.CATEGORY_EXISTS);
        }
        return categoryRepository.save(category);
    }

    public Page<Category> findAllCategory(int page, int size){
        return categoryRepository.findAll(PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Category findCategory (String name){
        return categoryRepository.findByName(name);
    }

    private boolean isCategoryNameExists(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }


}
