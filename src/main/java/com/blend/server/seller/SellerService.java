package com.blend.server.seller;

import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.OrderRepository;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import com.blend.server.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    private final SellerMapper sellerMapper;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final CustomAuthorityUtils authorityUtils;
    private final OrderRepository orderRepository;

    public Seller createSeller (Seller seller) {
        log.info("---Creating Seller---");
        verifiedJoinEmail(seller);
        verifiedRegNumber(seller);
        setEncodedPassword(seller);
        setRole(seller);
        Seller savedSeller = sellerRepository.save(seller);
        log.info("Seller created : {}", savedSeller.getId());

        return savedSeller;
    }

    public void setRole(Seller seller){
        List<String> roles = authorityUtils.createSellerRoles(seller);
        seller.setRoles(roles);
        log.info("Roles set for seller {}: {}", seller.getId(), roles);
    }

    public Seller updateSeller (Seller seller){
        log.info("---Updating Seller :{} ---",seller);
        Seller findSeller = findVerifiedSeller(seller.getId());

        Optional.ofNullable(seller.getPassword())
                .ifPresent(password -> findSeller.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(seller.getName())
                .ifPresent(name -> findSeller.setName(name));
        Optional.ofNullable(seller.getAddress())
                .ifPresent(address -> findSeller.setAddress(address));
        Optional.ofNullable(seller.getPhone())
                .ifPresent(phone -> findSeller.setPhone(phone));
        log.info("Updated Seller: {}", findSeller);

        return sellerRepository.save(findSeller);
    }

    private void verifiedJoinEmail(Seller seller) {
        String email = seller.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        optionalUser.ifPresent(sellers -> {
            log.warn("Email {} already exists", email);
            throw new BusinessLogicException(ExceptionCode.USER_EMAIL_EXISTS);
        });
    }

    private void verifiedRegNumber (Seller seller) {
        String regNumber = seller.getRegNumber();
        Optional<Seller> optionalSeller = sellerRepository.findByRegNumber(regNumber);
        optionalSeller.ifPresent(register -> {
            log.warn("Registration number {} already exists", regNumber);
            throw new BusinessLogicException(ExceptionCode.REGNUMBER_EXISTS);
        });
    }

    private void setEncodedPassword(Seller seller) {
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        log.info("Password encoded: {}", seller.getId());
    }

    public Seller findVerifiedSeller(long id) {
        log.info("---Finding Verified Seller---");
        Seller findSeller = sellerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Seller not found with ID: {}", id);
                    throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
                });
        verifiedApproveSeller(findSeller);
        log.info("Verified Seller: {}", id);
        return findSeller;
    }

    public void verifiedApproveSeller(Seller seller){
        log.info("---Verifying Approved Seller---");
        if(seller.getSellerStatus().getNumber() == 1){
            log.warn("Seller {} is waiting for approval", seller.getId());
            throw new BusinessLogicException(ExceptionCode.SELLER_WAIT);
        }else if(seller.getSellerStatus().getNumber() == 3){
            log.warn("Seller {} has been rejected", seller.getId());
            throw new BusinessLogicException(ExceptionCode.SELLER_REJECTED);
        }
    }

    public Page<Product> findProducts(int size, int page,Seller seller) {
        log.info("---Searching On Sale Products---");
        Seller findSeller = findVerifiedSeller(seller.getId());
        return productRepository.findAllBySellerAndProductStatus(findSeller,
                Product.ProductStatus.SALE, PageRequest.of(page-1,size, Sort.by("id").descending()));
    }

    public void deleteProduct(long id, Seller seller) {
        log.info("Deleting Product", id);
        Product product = productRepository.findById(id)
                .orElseThrow(()->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        if(product.getProductStatus().getStatusNumber() == 3)
            throw new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
        if(product.getSeller().getId() != seller.getId()) {
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_ALLOWED);
        }
        product.setProductStatus(Product.ProductStatus.PRODUCT_DELETE);
        productRepository.save(product);
        log.info("Deleted Product {}",id);
    }
}
