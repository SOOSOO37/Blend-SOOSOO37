package com.blend.server.user;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserMapper mapper;

    private final UserService service;

    private final CustomAuthorityUtils customAuthorityUtils;

    private final static String USER_DEFAULT_URL = "/users";

    @PostMapping("/sign-up")
    public ResponseEntity createUser(@Valid @RequestBody UserPostDto userPostDto){
        User user = service.createUser(mapper.userPostDtoToUser(userPostDto));

        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/edit/nickname")
    public ResponseEntity updateNickName(@Valid @RequestBody UserPatchDto userPatchDto,
                                         @AuthenticationPrincipal User user){
            userPatchDto.setId(user.getId());
            User findUser = service.updateNickName(mapper.userPatchDtoToUser(userPatchDto));
            UserResponseDto response = mapper.userToUserResponseDto(findUser);
            return new ResponseEntity(response, HttpStatus.OK);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UserPatchDto userPatchDto,
                                         @AuthenticationPrincipal User user) {
        userPatchDto.setId(user.getId());
        User findUser = service.updatePassword(mapper.userPatchDtoToUser(userPatchDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/my-page")
    public ResponseEntity findUser(@AuthenticationPrincipal User user) {
        User findUser = service.findUser(user);
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        service.deleteUser(user);

        return ResponseEntity.noContent().build();
    }
}
