package com.blend.server.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category){
        return categoryRepository.save(category);
    }

    public Page<Category> findAllCategory(int page, int size){
        return categoryRepository.findAll(PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Category findCategory (String name){
        return categoryRepository.findByName(name);
    }

}
