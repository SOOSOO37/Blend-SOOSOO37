package com.blend.server.Product.category;

import com.blend.server.Product.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

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
