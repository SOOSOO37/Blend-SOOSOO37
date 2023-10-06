package com.blend.server.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category categoryPostDtoToCategory (CategoryCreateDto categoryCreateDto);


}
