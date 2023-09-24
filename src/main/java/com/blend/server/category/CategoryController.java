package com.blend.server.category;

import com.blend.server.utils.UriCreator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Api(tags = "Category API Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper mapper;

    private final static String CATEGORY_DEFAULT_URL = "/categories";

    @PostMapping
    @ApiOperation(value = "카테고리 생성 API")
    public ResponseEntity createCategory(@RequestBody CategoryCreateDto categoryCreateDto){

        Category category = categoryService.createCategory(mapper.categoryPostDtoToCategory(categoryCreateDto));

        URI location = UriCreator.createUri(CATEGORY_DEFAULT_URL,category.getId());

        return ResponseEntity.created(location).build();

    }

    @ApiOperation(value = "전체 카테고리 조회 API")
    @GetMapping("/all")
    public ResponseEntity findAllCategories(@RequestParam int page,
                                            @RequestParam int size){


        Page<Category> categories = categoryService.findAllCategory(page -1, size);
        List<Category> productList = categories.getContent();

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
