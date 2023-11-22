package com.blend.server.seller;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductResponseDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    Seller sellerPostDtoToSeller (SellerPostDto sellerPostDto);

    Seller sellerPatchDtoToSeller (SellerPatchDto sellerPatchDto);

}
