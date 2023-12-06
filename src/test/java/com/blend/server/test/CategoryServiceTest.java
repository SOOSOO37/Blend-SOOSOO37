package com.blend.server.test;

import com.blend.server.category.Category;
import com.blend.server.category.CategoryRepository;
import com.blend.server.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @DisplayName("카테고리 생성 테스트")
    @Test
    public void createCategoryTest(){
        Category category = TestObjectFactory.createCategory();

        when(categoryRepository.existsByName(category.getName())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals(category.getName(),result.getName());

    }

    @DisplayName("카테고리 전체 조회 테스트")
    @Test
    public void findAllCategoryTest(){

        List<Category> categoryList = new ArrayList<>();
        Category category = TestObjectFactory.createCategory();
        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("하의");
        categoryList.add(category);
        categoryList.add(secondCategory);


        when(categoryRepository.findAll(PageRequest.of(0, 10, Sort.by("id").descending()))).thenReturn(new PageImpl<>(categoryList));

        Page<Category> result = categoryService.findAllCategory(0,10);

        assertNotNull(result);
        assertEquals(categoryList.size(), result.getContent().size());

    }

    @DisplayName("카테고리 이름 조회 테스트")
    @Test
    public void findCategoryTest(){

        Category category = new Category();
        category.setId(1L);
        category.setName("하의");

        when(categoryRepository.findByName(category.getName())).thenReturn((category));

        Category result = categoryService.findCategory(category.getName());

        assertNotNull(result);
        assertEquals("하의", result.getName());

    }

}
