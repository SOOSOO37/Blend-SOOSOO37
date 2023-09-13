package com.blend.server.Product.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category categoryPostDtoToCategory (CategoryPostDto categoryPostDto);


}
