package com.blend.server.seller;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.order.Order;
import com.blend.server.order.OrderRepository;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import com.blend.server.user.UserService;
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

@RequiredArgsConstructor
@Transactional
@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final CustomAuthorityUtils authorityUtils;
    private final OrderRepository orderRepository;

    public Seller createSeller (Seller seller) {
        verifiedJoinEmail(seller);
        verifiedRegNumber(seller);
        setEncodedPassword(seller);

        return sellerRepository.save(seller);
    }

    public Seller updateSeller (Seller seller){
        Seller findSeller = findVerifiedSeller(seller.getId());

        BeanUtils.copyProperties(seller, findSeller);

        return sellerRepository.save(findSeller);
    }

    private void verifiedJoinEmail(Seller seller) {
        String email = seller.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        optionalUser.ifPresent(sellers -> {
            throw new BusinessLogicException(ExceptionCode.USER_EMAIL_EXISTS);
        });
    }

    private void verifiedRegNumber (Seller seller) {
        String regNumber = seller.getRegNumber();
        Optional<Seller> optionalSeller = sellerRepository.findByRegNumber(regNumber);
        optionalSeller.ifPresent(register -> {
            throw new BusinessLogicException(ExceptionCode.REGNUMBER_EXISTS);
        });
    }

    private void setEncodedPassword(Seller seller) {
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
    }

    public Seller findVerifiedSeller(long id) {
        Seller findSeller = sellerRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BusinessLogicException(ExceptionCode.SELLER_NOT_FOUND);
                });
        verifiedApproveSeller(findSeller);
        return findSeller;
    }

    public void verifiedApproveSeller(Seller seller){
        if(seller.getSellerStatus().getNumber() == 1){
            throw new BusinessLogicException(ExceptionCode.SELLER_WAIT);
        }else if(seller.getSellerStatus().getNumber() == 3){
            throw new BusinessLogicException(ExceptionCode.SELLER_REJECTED);
        }
    }

    public Seller findSeller(long id) {
        Seller findSeller = findVerifiedSeller(id);
        return findSeller;
    }

    public Page<Product> findProducts(int size, int page , long id) {
        Seller findSeller = findVerifiedSeller(id);
        return productRepository.findAllBySellerIdAndProductStatusNotLike(findSeller.getId(),
                Product.ProductStatus.SOLDOUT, PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Page<Order> findOrders(int page, int size, long id){
        Seller findSeller = findVerifiedSeller(id);
        return orderRepository.findAllByOrderStatusAndUserId(Order.OrderStatus.ORDER_DONE, findSeller.getId(),
                PageRequest.of(page,size, Sort.by("id").descending()));
    }

    public Order findOrderStatus(Long orderId, Order.OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));

        verifiedOrderStatus(order, orderStatus);

        return order;
    }

    private void verifiedOrderStatus(Order order, Order.OrderStatus orderStatus) {

        int verifiedStatusNumber = order.getOrderStatus().getNumber();
        int targetStatusNumber = orderStatus.getNumber();

        if (verifiedStatusNumber >= targetStatusNumber) {
            throw new BusinessLogicException(ExceptionCode.DO_NOT_NEXTSTEP);
        }
    }


    public void deleteProduct(long id, long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->
                        new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));
        if(product.getProductStatus().getStatusNumber() == 3)
            throw new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
        if(product.getSeller().getId() != sellerId) {
            throw new BusinessLogicException(ExceptionCode.SELLER_NOT_ALLOWED);
        }
        product.setProductStatus(Product.ProductStatus.PRODUCT_DELETE);
        productRepository.save(product);
    }
}
