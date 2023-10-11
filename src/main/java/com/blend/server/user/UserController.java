package com.blend.server.user;

import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
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
                                         @AuthenticationPrincipal User user) {
        userPatchDto.setId(user.getId());
        User findUser = service.updateUser(mapper.userPatchDtoToUser(userPatchDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @PatchMapping("/edit/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UserPatchDto userPatchDto,
                                         @AuthenticationPrincipal User user) {
        userPatchDto.setId(user.getId());
        User findUser = service.updateUser(mapper.userPatchDtoToUser(userPatchDto));
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/my-page")
    public ResponseEntity findUser(@AuthenticationPrincipal User user) {
        User findUser = service.findUser(user);
        UserResponseDto response = mapper.userToUserResponseDto(findUser);

        return new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findUsers(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = service.findUsers(page - 1, size);
        List<User> userList = userPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.usersToUserResponseDtos(userList), userPage), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        service.deleteUser(user);

        return ResponseEntity.noContent().build();
    }
}
