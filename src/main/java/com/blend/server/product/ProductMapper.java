package com.blend.server.product;

import com.blend.server.category.Category;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.productImage.ProductImage;
import com.blend.server.review.ReviewDetailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    Product productStatusPatchDtoToProduct(ProductStatusUpdateDto productStatusUpdateDto);

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


        List<ReviewDetailResponseDto> response= product.getReviews().stream()
                        .map(review -> {
                            ReviewDetailResponseDto reviewDetailResponseDto = new ReviewDetailResponseDto();
                            List<String> links = review.getImages().stream()
                                    .map(reviewImage -> {
                                        String link = domain+"/review-images/"+reviewImage.getId();
                                        return link;
                                    })
                                    .collect(Collectors.toList());
                            reviewDetailResponseDto.setId(review.getId());
                            reviewDetailResponseDto.setTitle(review.getTitle());
                            reviewDetailResponseDto.setReviewStatus(review.getReviewStatus());
                            reviewDetailResponseDto.setScore(review.getScore());
                            reviewDetailResponseDto.setContent(review.getContent());
                            reviewDetailResponseDto.setCreatedAt(review.getCreatedAt());
                            reviewDetailResponseDto.setReviewImageUrls(links);
                            return reviewDetailResponseDto;
                        })
                                .collect(Collectors.toList());
        productDetailResponseDto.setReviewList(response);

        BeanUtils.copyProperties(product, productDetailResponseDto);

        return productDetailResponseDto;

    }
    default List<ProductResponseDto> productsToProductResponseDtos(List<Product> products){
       if (products == null){
           throw new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
       }
       List<ProductResponseDto> list = new ArrayList<ProductResponseDto>(products.size());
       for (Product product : products) {
           list.add(productToProductResponseDto(product));
       }
       return list;
    }

    default List<ProductImage> multipartFilesToProductImages(List<MultipartFile> productImages){
        if(productImages == null){
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
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
