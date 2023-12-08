package com.blend.server.test;

import com.blend.server.admin.AdminService;
import com.blend.server.review.Review;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.user.User;
import com.blend.server.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("관리자 가입신청 한 판매자 조회 테스트")
    @Test
    public void findSellersTest(){
        List<Seller> sellers = Arrays.asList(
                createSeller(1L, Seller.SellerStatus.SELLER_WAIT),
                createSeller(2L, Seller.SellerStatus.SELLER_WAIT),
                createSeller(3L, Seller.SellerStatus.SELLER_WAIT)
        );

        when(sellerRepository.findAllBySellerStatus(
                Seller.SellerStatus.SELLER_WAIT,
                PageRequest.of(0, 10, Sort.by("createdAt").descending())
        )).thenReturn(new PageImpl<>(sellers));

        Page<Seller> result = adminService.findSellers(0, 10);

        assertNotNull(result);
        assertEquals(sellers.size(), result.getContent().size());


    }

    private Seller createSeller(long id ,Seller.SellerStatus sellerStatus){
        Seller seller = new Seller();
        seller.setId(id);
        seller.setSellerStatus(sellerStatus);
        return seller;
    }

    @DisplayName("관리자 전체 유저 조회 테스트")
    @Test
    public void findUsersTest(){
        List<User> users = Arrays.asList(
                createUser(1L, User.UserStatus.ACTIVE),
                createUser(2L, User.UserStatus.ACTIVE),
                createUser(3L, User.UserStatus.ACTIVE)
        );

        when(userRepository.findAllByUserStatus(
                User.UserStatus.ACTIVE,
                PageRequest.of(0, 10, Sort.by("id").descending())
        )).thenReturn(new PageImpl<>(users));

        Page<User> result = adminService.findUsers(0, 10);

        assertNotNull(result);
        assertEquals(users.size(), result.getContent().size());
    }

    private User createUser(long id , User.UserStatus userStatus){
        User user = new User();
        user.setId(id);
        user.setUserStatus(userStatus);
        return user;
    }

    @DisplayName("관리자 판매자 가입 승인 테스트")
    @Test
    public void approveSellerTest(){

        Seller seller = new Seller();
        seller.setId(1L);

        when(sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller result = adminService.approveSeller(seller.getId());

        assertEquals(Seller.SellerStatus.SELLER_APPROVE, result.getSellerStatus());
    }

    @DisplayName("관리자 판매자 가입 거절 테스트")
    @Test
    public void rejectedSellerTest(){
        Seller seller = new Seller();
        seller.setId(1L);

        when(sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller result = adminService.rejectedSeller(seller.getId());

        assertEquals(Seller.SellerStatus.SELLER_REJECTED, result.getSellerStatus());
    }
}
