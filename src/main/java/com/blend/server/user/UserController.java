package com.blend.server.user;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Api(tags = "User API Controller")
@Slf4j
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserMapper mapper;

    private final UserService service;

    private final CustomAuthorityUtils customAuthorityUtils;

    private final static String USER_DEFAULT_URL = "/users";

    @ApiOperation(value = "회원가입 API")
    @PostMapping("/sign-up")
    public ResponseEntity createUser(@Valid @RequestBody UserPostDto userPostDto){

        log.info("---Sign Up User---");
        User user = service.createUser(mapper.userPostDtoToUser(userPostDto));
        log.info("User created: {}", user);
        URI location = UriCreator.createUri(USER_DEFAULT_URL, user.getId());
        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "닉네임 수정 API")
    @PatchMapping("/edit/nickname")
    public ResponseEntity updateNickName(@Valid @RequestBody UserPatchDto userPatchDto,
                                         @AuthenticationPrincipal User user){
        log.info("---Update Nickname---");
        userPatchDto.setId(user.getId());
        User findUser = service.updateNickName(mapper.userPatchDtoToUser(userPatchDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);
        log.info("Nickname updated for User {}: {}", user.getId(), response);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 수정 API")
    @PatchMapping("/edit/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UserPatchDto userPatchDto,
                                         @AuthenticationPrincipal User user) {
        log.info("---Update Password---");
        userPatchDto.setId(user.getId());
        User findUser = service.updatePassword(mapper.userPatchDtoToUser(userPatchDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);
        log.info("Password updated for User {}: {}", user.getId(), response);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @ApiOperation(value = "마이페이지 유저 정보 조회 API")
    @GetMapping("/my-page")
    public ResponseEntity findUser(@AuthenticationPrincipal User user) {
        log.info("---Find User---");
        User findUser = service.findUser(user);
        UserResponseDto response = mapper.userToUserResponseDto(findUser);
        log.info("User information was searched {}: {}", user.getId(), response);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @ApiOperation(value = "유저 탈퇴 API")
    @DeleteMapping
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        log.info("---Deleting User---");
        service.deleteUser(user);
        log.info("Deleted Product {}",user.getId());
        return ResponseEntity.noContent().build();
    }
}
