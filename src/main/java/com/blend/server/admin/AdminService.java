package com.blend.server.admin;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    public Page<Seller> findSellers(int page, int size){
        log.info("Finding sellers with status SELLER_WAIT - Page: {}, Size: {}", page, size);
        Page<Seller> sellers = sellerRepository.findAllBySellerStatus(Seller.SellerStatus.SELLER_WAIT,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        log.info("Found Sellers - Page: {}, Size: {}, Total Sellers: {}",
                page, size, sellers.getTotalElements());
        return sellers;
    }

    public Page<User> findUsers(int page, int size){
        log.info("Finding Users - Page: {}, Size: {}", page, size);
        Page<User> users = userRepository.findAllByUserStatus(User.UserStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("id").descending()));
        log.info("Found Users - Page: {}, Size: {}, Total Users: {}",
                page, size, users.getTotalElements());
        return users;
    }

    public Seller approveSeller(long id){
        log.info("Approving seller - SellerId: {}", id);
        Seller findSeller = findVerifiedSeller(id);
        findSeller.setSellerStatus(Seller.SellerStatus.SELLER_APPROVE);
        sellerRepository.save(findSeller);
        log.info("Approved Seller- SellerId: {}", id);
        return findSeller;
    }

    public Seller rejectedSeller(long id){
        log.info("Rejecting seller - SellerId: {}", id);
        Seller findSeller = findVerifiedSeller(id);
        findSeller.setSellerStatus(Seller.SellerStatus.SELLER_REJECTED);
        sellerRepository.save(findSeller);
        log.info("Rejected seller - SellerId: {}", id);
        return findSeller;
    }

    private Seller findVerifiedSeller(Long id) {
        log.info("Verified seller - SellerId: {}", id);
        Optional<Seller> optionalSeller = sellerRepository.findById(id);
        Seller findSeller = optionalSeller.orElseThrow(() -> new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND));
        log.info("Verified seller - SellerId: {}", id);
        return findSeller;
    }
}
