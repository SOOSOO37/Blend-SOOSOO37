package com.blend.server.admin;

import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerPatchDto;
import com.blend.server.seller.SellerPostDto;
import com.blend.server.seller.SellerResponseDto;
import com.blend.server.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminResponseDto sellerToAdminResponseDto(Seller seller);

   List<AdminResponseDto> sellersToAdminResponseDto(List<Seller> sellers);





}
