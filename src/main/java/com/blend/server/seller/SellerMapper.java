package com.blend.server.seller;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    Seller sellerPostDtoToSeller (SellerPostDto sellerPostDto);

    Seller sellerPatchDtoToSeller (SellerPatchDto sellerPatchDto);

    SellerResponseDto sellerToSellerResponseDto(Seller seller);
}
