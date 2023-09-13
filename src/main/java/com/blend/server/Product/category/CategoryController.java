package com.blend.server.Product.category;

import com.blend.server.Product.global.dto.MultiResponseDto;
import com.blend.server.Product.product.Product;
import com.blend.server.Product.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper mapper;

    private final static String CATEGORY_DEFAULT_URL = "/categories";

    public CategoryController(CategoryService categoryService, CategoryMapper mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryPostDto categoryPostDto){

        Category category = categoryService.createCategory(mapper.categoryPostDtoToCategory(categoryPostDto));

        URI location = UriCreator.createUri(CATEGORY_DEFAULT_URL,category.getId());

        return ResponseEntity.created(location).build();

    }

    @GetMapping("/all")
    public ResponseEntity findAllCategories(@RequestParam int page,
                                            @RequestParam int size){


        Page<Category> categories = categoryService.findAllCategory(page -1, size);
        List<Category> productList = categories.getContent();

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
