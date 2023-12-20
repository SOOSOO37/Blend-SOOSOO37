package com.blend.server.user;

import com.blend.server.cart.Cart;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.seller.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    private final CustomAuthorityUtils authorityUtils;

    private final PasswordEncoder passwordEncoder;


    public User createUser(User user){
        log.info("---Creating User---");
        checkExistEmail(user.getEmail());
        checkExistNickName(user.getNickName());
        setEncodedPassword(user);
        setRole(user);
        makeUserCart(user);
        manageCart(user);
        User savedUser = userRepository.save(user);
        log.info("User created : {}", savedUser.getId());
        return savedUser;

    }
    public User updateNickName(User user){
        log.info("---Updating User :{} ---",user);
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);
        Optional.ofNullable(user.getNickName())
                .ifPresent(nickName -> findUser.setNickName(nickName));
        log.info("Updated User: {}", findUser);

        return userRepository.save(findUser);
    }

    public User updatePassword(User user){
        log.info("---Updating User :{} ---",user);
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);
        Optional.ofNullable(user.getPassword())
                .ifPresent(password -> findUser.setPassword(passwordEncoder.encode(password)));
        log.info("Updated User: {}", findUser);
        return userRepository.save(findUser);
    }

    public User findUser(User user) {
        log.info("---Inquiring User---",user);
        User findUser = findVerifiedUser(user.getId());
        log.info("Find User: {}",user);
        return findUser;
    }



    public void deleteUser(User user){
        log.info("Deleting User", user);
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);

        findUser.setUserStatus(User.UserStatus.QUIT);
        log.info("Deleted User {}",findUser);
        userRepository.save(findUser);
    }

    public void setRole(User user){
        List<String> roles = authorityUtils.createRoles(user);
        user.setRoles(roles);
        log.info("Roles set for user {}: {}", user.getId(), roles);
    }

    public void setEncodedPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password encoded: {}", user.getId());
    }

    public User findVerifiedUser(long id) {
        log.info("---Finding Verified User---");
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
                });
        verifiedActiveUser(findUser);
        log.info("Verified User: {}", id);
        return findUser;
    }
    private static void verifiedActiveUser (User user){
        if(user.getUserStatus().getNumber() == 2){
            log.warn("Sleeper account found for user : {}", user.getId());
            throw new BusinessLogicException(ExceptionCode.SLEEPER_ACCOUNT);
        }else if(user.getUserStatus().getNumber() ==3){
            log.warn("User quit found : {}", user.getId());
            throw new BusinessLogicException(ExceptionCode.USER_QUIT);
        }
    }

    public void checkExistEmail(String email){
        log.info("Checking email {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent())
            log.warn("Email {} already exists", email);
            throw new BusinessLogicException(ExceptionCode.USER_EMAIL_EXISTS);
    }

    public void checkExistNickName(String nickName){
        log.info("Checking nickname {}", nickName);
        Optional<User> optionalUser = userRepository.findByNickName(nickName);

        if(optionalUser.isPresent())
            log.warn("Nickname {} already exists", nickName);
            throw new BusinessLogicException(ExceptionCode.USER_NICKNAME_EXISTS);
    }

    public void manageCart(User user){
        log.info("Managing cart {}", user.getId());
        if(user.getRoles().contains("ADMIN")|| user.getRoles().contains("SELLER")){
            log.info("User {} is an admin or seller", user.getId());
            user.setCart(null);
        }
    }

    public void makeUserCart(User user) {
        log.info("Creating cart {}", user.getId());
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
    }

    private void checkUserStatus(User user){
        log.info("Checking status {}", user.getId());
        if(user.getUserStatus() == User.UserStatus.QUIT)
            log.warn("Quit User {}", user.getId());
            throw new BusinessLogicException(ExceptionCode.USER_QUIT);
    }
}
