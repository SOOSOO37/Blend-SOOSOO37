package com.blend.server.Product;

import com.blend.server.category.Category;
import com.blend.server.productImage.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    default ProductDetailResponseDto productToProductDetailResponseDto(Product product,String domain){
        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto();

        productDetailResponseDto.setCategoryId(product.getCategory().getId());
        productDetailResponseDto.setName(product.getCategory().getName());
        //이미지의 상세 정보에 접근 URL을 생성
        List<String> productImageLink = product.getProductImages().stream()
                .map(image -> {
                    String link = domain+"/images/" + image.getId();
                    return link;
                })
                .collect(Collectors.toList());

        productDetailResponseDto.setImageLinks(productImageLink);
        BeanUtils.copyProperties(product, productDetailResponseDto);

        return productDetailResponseDto;

    }
    List<ProductResponseDto> productsToProductResponseDtos(List<Product> products);

    default List<ProductImage> multipartFilesToProductImages(List<MultipartFile> productImages){
        if(productImages == null){
            return null;
        }
        List<ProductImage> productImageList = productImages.stream()
                .map(multipartFile -> {
                    ProductImage productImage = new ProductImage();
                    try {
                        productImage.setImage(multipartFile.getBytes());
                        productImage.setType(multipartFile.getContentType());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return productImage;
                })
                .collect(Collectors.toList());

        return productImageList;
    }
}
