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

    Admin adminPostDtoToAdmin (AdminPostDto adminPostDto);

    Admin adminPatchDtoToAdmin (AdminPatchDto adminPatchDto);

    AdminResponseDto adminToAdminResponseDto(Admin admin);
    AdminResponseDto sellerToAdminResponseDto(Seller seller);

    AdminSellerPatchDto sellerToSellerUpdateDto(Seller seller);

    @Mapping(source = "id", target = "seller.id")
    User sellerUpdateDtoToUser(AdminSellerPatchDto updateToUser);

   List<AdminResponseDto> sellersToAdminResponseDto(List<Seller> sellers);





}
