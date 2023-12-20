package com.blend.server.admin;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.global.response.MultiResponseDto;
import com.blend.server.global.utils.UriCreator;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerMapper;
import com.blend.server.seller.SellerService;
import com.blend.server.user.User;
import com.blend.server.user.UserMapper;
import com.blend.server.user.UserResponseDto;
import com.blend.server.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Api(tags = "Admin API Controller")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admins")
@RestController
public class AdminController {

    private final  AdminMapper mapper;
    private final AdminService adminService;
    private final UserMapper userMapper;

    @ApiOperation(value = "가입대기 판매자 전체 조회 API")
    @GetMapping("/all")
    public ResponseEntity findSellers(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Finding all waiting Seller - Page: {}, Size: {}", page, size);
        Page<Seller> sellerPage = adminService.findSellers(page - 1, size);
        List<Seller> sellerList = sellerPage.getContent();
        log.info("Found All Sellers - Page: {}, Size: {}, Total Users: {}",
                page, size, sellerPage.getTotalElements());

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.sellersToAdminResponseDto(sellerList), sellerPage), HttpStatus.OK);
    }

    @ApiOperation(value = "전체 유저 조회 API")
    @GetMapping("/find")
    public ResponseEntity findUsers(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {

        log.info("Finding all users - Page: {}, Size: {}", page, size);
        Page<User> userPage = adminService.findUsers(page - 1, size);
        List<User> userList = userPage.getContent();
        log.info("Found All users - Page: {}, Size: {}, Total Users: {}",
                page, size, userPage.getTotalElements());
        return new ResponseEntity<>(new MultiResponseDto<>(userMapper.usersToUserResponseDtos(userList), userPage), HttpStatus.OK);
    }

    @ApiOperation(value = "판매자 가입승인 API")
    @PatchMapping("/approval/{seller-id}")
    public ResponseEntity approveSeller(@PathVariable("seller-id")long sellerId){
        log.info("Approving seller - SellerId: {}", sellerId);
        Seller updateSeller = adminService.approveSeller(sellerId);
        AdminResponseDto response = mapper.sellerToAdminResponseDto(updateSeller);
        log.info("Approved Seller - SellerId: {}", sellerId);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @ApiOperation(value = "판매자 가입거절 API")
    @PatchMapping("/rejected/{seller-id}")
    public ResponseEntity rejectedSellerStatus(@PathVariable("seller-id") long sellerId) {
        log.info("Rejecting seller - SellerId: {}", sellerId);
        Seller updateSeller = adminService.rejectedSeller(sellerId);
        AdminResponseDto response = mapper.sellerToAdminResponseDto(updateSeller);
        log.info("Rejected seller - SellerId: {}", sellerId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
