package com.blend.server.Product;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product productPostDtoToProduct(ProductPostDto productPostDto);

    Product productPatchDtoToProduct(ProductPatchDto productPatchDto);

    ProductResponseDto productToProductResponseDto(Product product);

    ProductDetailResponseDto productToProductDetailResponseDto(Product product);

    List<ProductResponseDto> productsToProductResponseDtos(List<Product> products);
}
