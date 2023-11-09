package com.blend.server.security.userdetail;
import com.blend.server.Product.Product;
import com.blend.server.admin.AdminRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Seller> optionalSeller = sellerRepository.findByEmail(username);
        Seller seller = optionalSeller.orElse(null);

        Optional<User> optionalUser = userRepository.findByEmail(username);
        User user = optionalUser.orElse(null);

        if (seller != null && seller.getSellerStatus() != Seller.SellerStatus.SELLER_REJECTED) {
            return new CustomSellerDetails(seller);
        } else if (user != null && user.getUserStatus() != User.UserStatus.QUIT) {
            return new CustomUserDetails(user);
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }
    private final class CustomSellerDetails extends Seller implements UserDetails {
        CustomSellerDetails(Seller seller) {
            setId(seller.getId());
            setEmail(seller.getEmail());
            setPassword(seller.getPassword());
            setAccountNumber(seller.getAccountNumber());
            setRoles(seller.getRoles());
            setAddress(seller.getAddress());
            setBank(seller.getBank());
            setRegNumber(seller.getRegNumber());
            setPhone(seller.getPhone());
            setSellerStatus(seller.getSellerStatus());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getRoles());
        }
        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // 판매자의 계정 만료 여부에 따른 로직 추가
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // 판매자의 계정 잠김 여부에 따른 로직 추가
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // 판매자의 자격 증명 만료 여부에 따른 로직 추가
        }

        @Override
        public boolean isEnabled() {
            return true; // 판매자의 계정 활성화 여부에 따른 로직 추가
        }
    }

    private final class CustomUserDetails extends User implements UserDetails {

        CustomUserDetails(User user) {
            setId(user.getId());
            setEmail(user.getEmail());
            setPassword(user.getPassword());
            setNickName(user.getNickName());
            setRoles(user.getRoles());
            setUserStatus(user.getUserStatus());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getRoles());
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // 사용자의 계정 만료 여부에 따른 로직 추가
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // 사용자의 계정 잠김 여부에 따른 로직 추가
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // 사용자의 자격 증명 만료 여부에 따른 로직 추가
        }

        @Override
        public boolean isEnabled() {
            return true; // 사용자의 계정 활성화 여부에 따른 로직 추가
        }
    }
}
