package com.blend.server.user;

import com.blend.server.cart.Cart;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default User userPostDtoToUser(UserPostDto userPostDto){
        if(userPostDto == null){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        Cart cart = new Cart();
        User user = new User(cart);

        BeanUtils.copyProperties(userPostDto,user);

        return user;
    }

    User userPatchDtoToUser(UserPatchDto userPatchDto);

    @Mapping(source = "userStatus.description", target = "userStatus")
    UserResponseDto userToUserResponseDto(User user);

    List<UserResponseDto> usersToUserResponseDtos(List<User> userList);
}
