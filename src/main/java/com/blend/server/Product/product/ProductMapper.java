package com.blend.server.Product.product;

import com.blend.server.Product.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default Product productPostDtoToProduct(ProductPostDto productPostDto){

        Category category = new Category();

        Product product = new Product();

        product.setCategory(category);
        product.setBrand(productPostDto.getBrand());
        product.setProductName(productPostDto.getProductName());
        product.setPrice(productPostDto.getPrice());
        product.setSalePrice(productPostDto.getSalePrice());
        product.setProductStatus(productPostDto.getProductStatus());
        product.setImage(productPostDto.getImage());
        product.setReviewCount(productPostDto.getReviewCount());
        product.setLikeCount(productPostDto.getLikeCount());
        product.setProductCount(productPostDto.getProductCount());
        product.setSizeInfo(productPostDto.getSizeInfo());
        product.setInfo(productPostDto.getInfo());

        return product;
    }

       @Mapping(source = "categoryId", target = "category.id")
      Product productPatchDtoToProduct(ProductPatchDto productPatchDto);



    default ProductResponseDto productToProductResponseDto(Product product){
        ProductResponseDto productResponseDto = new ProductResponseDto();

        productResponseDto.setId(product.getId());
        productResponseDto.setBrand(product.getBrand());
        productResponseDto.setProductName(product.getProductName());
        productResponseDto.setCategoryId(product.getCategory().getId());
        productResponseDto.setName(product.getCategory().getName());
        productResponseDto.setRanking(product.getRanking());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setSalePrice(product.getSalePrice());
        productResponseDto.setProductStatus(product.getProductStatus());
        productResponseDto.setImage(product.getImage());
        productResponseDto.setCreatedAt(product.getCreatedAt());
        productResponseDto.setModifiedAt(product.getModifiedAt());

        return productResponseDto;
    }

    default ProductDetailResponseDto productToProductDetailResponseDto(Product product){
        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto();

        productDetailResponseDto.setId(product.getId());
        productDetailResponseDto.setBrand(product.getBrand());
        productDetailResponseDto.setProductName(product.getProductName());
        productDetailResponseDto.setCategoryId(product.getCategory().getId());
        productDetailResponseDto.setName(product.getCategory().getName());
        productDetailResponseDto.setRanking(product.getRanking());
        productDetailResponseDto.setViewCount(product.getViewCount());
        productDetailResponseDto.setReviewCount(product.getReviewCount());
        productDetailResponseDto.setLikeCount(product.getLikeCount());
        productDetailResponseDto.setProductCount(product.getProductCount());
        productDetailResponseDto.setPrice(product.getPrice());
        productDetailResponseDto.setSalePrice(product.getSalePrice());
        productDetailResponseDto.setImage(product.getImage());
        productDetailResponseDto.setInfo(product.getInfo());
        productDetailResponseDto.setSizeInfo(product.getSizeInfo());
        productDetailResponseDto.setCreatedAt(product.getCreatedAt());
        productDetailResponseDto.setModifiedAt(product.getModifiedAt());

        return productDetailResponseDto;

    }

    List<ProductResponseDto> productsToProductResponseDtos(List<Product> products);
}
