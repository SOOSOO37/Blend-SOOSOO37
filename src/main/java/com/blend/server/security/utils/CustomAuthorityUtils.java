package com.blend.server.security.utils;

import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {

    @Value("${config.admin}")
    private String adminEmail;

    private final List<String> ADMIN_ROLES_STRING = List.of("ADMIN");

    private final List<String> USER_ROLES_STRING = List.of("USER");

    private final List<String> SELLER_ROLES_STRING = List.of("SELLER");

    private final List<GrantedAuthority> ADMIN_ROLES
            = AuthorityUtils.createAuthorityList("ROLE_ADMIN");

    private final List<GrantedAuthority> SELLER_ROLES
            = AuthorityUtils.createAuthorityList("ROLE_SELLER");
    private final List<GrantedAuthority> USER_ROLES =
            AuthorityUtils.createAuthorityList("ROLE_USER");

    //사용자의 권한을 확인 후 접근 권한을 부여하기 위해 GrantedAuthority 목록을 사용 /사용자가 어떤 역할인지 정의하기 위해 역할 목록을 GrantedAuthority로 변환
    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return authorities;
    }

    //db에서 객체를 가져와서 권한 부여
    public List<String> createRoles(User user) {
        if (user.getEmail().equals(adminEmail)) {
            return ADMIN_ROLES_STRING;
        }
        return USER_ROLES_STRING;
    }

    public List<String> createSellerRoles(Seller seller) {
        if (seller.getEmail().equals(adminEmail)) {
            return ADMIN_ROLES_STRING;
        } else {
            return SELLER_ROLES_STRING;
        }
    }

    public List<GrantedAuthority> createAuthorities(String email) {
        if (email.equals(adminEmail)) {
            return ADMIN_ROLES;
        }else
        return USER_ROLES;
    }

}
