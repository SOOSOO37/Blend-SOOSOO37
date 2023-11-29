package com.blend.server.test;

import com.blend.server.cart.Cart;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import com.blend.server.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomAuthorityUtils customAuthorityUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 모의 객체를 초기화하고 의존성 주입 (현재 테스트 클래스의 인스턴스를 초기화)
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유저 등록 테스트")
    @Test
    public void createUserTest(){
        User user = new User();
        user.setId(1L);
        user.setEmail("tester2@gmail.com");
        user.setNickName("닉네임1");
        user.setPassword("qwer1234!");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNickName(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        CustomAuthorityUtils customAuthorityUtils = mock(CustomAuthorityUtils.class);
        when(customAuthorityUtils.createRoles(any(User.class))).thenReturn(List.of("USER"));
        UserService userService = new UserService(userRepository, customAuthorityUtils, passwordEncoder);

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        assertEquals("tester2@gmail.com", savedUser.getEmail());
        assertEquals("닉네임1", savedUser.getNickName());
        assertEquals("USER", savedUser.getRoles().get(0));
        assertEquals(encodedPassword, user.getPassword());

    }

    @DisplayName("닉네임 수정 테스트")
    @Test
    public void updateNickNameTest(){
        User user = new User();
        user.setId(1L);
        user.setNickName("기존 닉네임");

        User findUser = new User();
        findUser.setId(1L);
        findUser.setNickName("변경된 닉네임");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateNickName(findUser);

        assertEquals(updatedUser.getId(), findUser.getId());
        assertEquals(updatedUser.getNickName(), findUser.getNickName());
    }

    @DisplayName("비밀번호 수정 테스트")
    @Test
    public void updatePasswordTest() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("기존 비밀번호");

        User findUser = new User();
        findUser.setId(1L);
        findUser.setPassword("변경된 비밀번호");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = userService.updatePassword(findUser);

        // Assert
        assertEquals(updatedUser.getId(), findUser.getId());
        assertNotEquals(updatedUser.getPassword(), findUser.getPassword()); // 비밀번호가 업데이트되었는지 확인
    }
    @DisplayName("회원 조회 테스트")
        @Test
        public void findUserTest() {
            // Arrange
            User user = new User();
            user.setId(1L);
            user.setNickName("TestUser");

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // Act
            User resultUser = userService.findUser(user);

            // Assert
            assertEquals(user.getId(), resultUser.getId());
            assertEquals(user.getName(), resultUser.getName());
        }

        @DisplayName("회원 삭제 테스트")
        @Test
        public void deleteUserTest() {

            User user = new User();
            user.setId(1L);
            user.setNickName("기존 닉네임");

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            userService.deleteUser(user);

            assertEquals(User.UserStatus.QUIT, user.getUserStatus());
        }

    @DisplayName("이메일 존재 테스트")
    @Test
    public void checkExistEmailTest() {

        String email = "test1@gmail.com";
        User user = new User();
        user.setEmail(email);
        // 이메일 주소로 사용자를 찾았을 때 찾은 사용자를 담은 Optional을 반환
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(BusinessLogicException.class, () -> userService.checkExistEmail(email));
    }

    @DisplayName("닉네임 존재 테스트")
    @Test
    public void checkExistNickNameTest(){

        String nickName = "닉네임";
        User user = new User();
        user.setNickName(nickName);

        when(userRepository.findByNickName(nickName)).thenReturn(Optional.of(user));

        assertThrows(BusinessLogicException.class, () -> userService.checkExistNickName(nickName));

    }

    @DisplayName("비밀번호 인코딩 테스트")
    @Test
    public void setEncodedPasswordTest() {
        // Arrange
        User user = new User();
        String password = "password";
        user.setPassword(password);

        // 모의 객체를 사용하여 passwordEncoder.encode 메소드가 호출될 때 설정한 값을 반환하도록 설정
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.setEncodedPassword(user);

        assertEquals(encodedPassword, user.getPassword());
    }

    @DisplayName("권한 부여 테스트")
    @Test
    public void setRoleAdmin() {
        CustomAuthorityUtils customAuthorityUtils = new CustomAuthorityUtils();
        String adminEmail = "tester1@gmail.com";
        customAuthorityUtils.setAdminEmail(adminEmail);

        User adminUser = mock(User.class);
        when(adminUser.getEmail()).thenReturn(adminEmail);

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("tester2@gmail.com");

        // Act
        List<String> roles = customAuthorityUtils.createRoles(adminUser);
        List<String> userRoles = customAuthorityUtils.createRoles(user);

        // Assert
        assertEquals(List.of("ADMIN"), roles);
        assertEquals(List.of("USER"), userRoles);

    }

    @DisplayName("카트 생성 테스트")
    @Test
    public void testMakeUserCart() {
        User user = new User();
        userService.makeUserCart(user);
        assertNotNull(user.getCart());
        assertEquals(user, user.getCart().getUser());
    }

    @DisplayName("관리자 카드 등록 X 테스트")
    @Test
    public void testManageCartTest() {

        User adminUser = new User();
        adminUser.setRoles(List.of("ADMIN"));
        adminUser.setCart(new Cart());

        userService.manageCart(adminUser);

        assertNull(adminUser.getCart());
    }


}
