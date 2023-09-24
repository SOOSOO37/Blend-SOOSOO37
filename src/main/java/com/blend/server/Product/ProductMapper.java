package com.blend.server.Product;

import com.blend.server.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default Product productPostDtoToProduct(ProductCreateDto productCreateDto){
        Category category = new Category();
        Product product = new Product();

        product.setCategory(category);
        BeanUtils.copyProperties(productCreateDto, product);

        return product;
    }

       @Mapping(source = "categoryId", target = "category.id")
      Product productPatchDtoToProduct(ProductUpdateDto productUpdateDto);

    static ProductResponseDto productToProductResponseDto(Product product){
        ProductResponseDto productResponseDto = new ProductResponseDto();

        BeanUtils.copyProperties(product, productResponseDto);

        productResponseDto.setCategoryId(product.getCategory().getId());
        productResponseDto.setName(product.getCategory().getName());

        return productResponseDto;
    }

    default ProductDetailResponseDto productToProductDetailResponseDto(Product product){
        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto();

        productDetailResponseDto.setCategoryId(product.getCategory().getId());
        productDetailResponseDto.setName(product.getCategory().getName());

        BeanUtils.copyProperties(product, productDetailResponseDto);

        return productDetailResponseDto;

    }

    List<ProductResponseDto> productsToProductResponseDtos(List<Product> products);
}
