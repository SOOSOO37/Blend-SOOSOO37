package com.blend.server.admin;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomAuthorityUtils authorityUtils;


    public Page<Seller> findSellers(int page, int size){
        return sellerRepository.findAllBySellerStatus(Seller.SellerStatus.SELLER_WAIT, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<User> findUsers(int page, int size){

        return userRepository.findAllByUserStatus(User.UserStatus.ACTIVE,PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Seller approveSeller(long id){
        Seller findSeller = findVerifiedSeller(id);
        findSeller.setSellerStatus(Seller.SellerStatus.SELLER_APPROVE);
        sellerRepository.save(findSeller);
        return findSeller;
    }

    public Seller rejectedSeller(long id){
        Seller findSeller = findVerifiedSeller(id);
        findSeller.setSellerStatus(Seller.SellerStatus.SELLER_REJECTED);
        sellerRepository.save(findSeller);
        return findSeller;
    }

    public void updateToUser(User user) {
        List<String> roles = authorityUtils.createRoles(user);
        user.setRoles(roles);
        userRepository.save(user);
    }

    private Seller findVerifiedSeller(Long id) {
        Optional<Seller> optionalSeller = sellerRepository.findById(id);

        return optionalSeller.orElseThrow(()-> new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND));
    }

    public Admin findVerifiedAdmin(long id){
        Optional<Admin> optionalAdmin = adminRepository.findById(id);

        return optionalAdmin.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ADMIN_NOT_FOUND));
    }
    private void verifyExistsEmail(String email) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        Optional<User> user = userRepository.findByEmail(email);
        Optional<Seller> seller = sellerRepository.findByEmail(email);

        if(admin.isPresent() || user.isPresent() || seller.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.USER_EMAIL_EXISTS);
        }
    }

}
