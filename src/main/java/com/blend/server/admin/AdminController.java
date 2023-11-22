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
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RequestMapping("/admins")
@RestController
public class AdminController {

    private final  AdminMapper mapper;
    private final AdminService adminService;
    private final UserMapper userMapper;

    @GetMapping("/all")
    public ResponseEntity findSellers(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {

        Page<Seller> sellerPage = adminService.findSellers(page - 1, size);
        List<Seller> sellerList = sellerPage.getContent();


        return new ResponseEntity<>(new MultiResponseDto<>(mapper.sellersToAdminResponseDto(sellerList), sellerPage), HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity findUsers(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {

        Page<User> userPage = adminService.findUsers(page - 1, size);
        List<User> userList = userPage.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(userMapper.usersToUserResponseDtos(userList), userPage), HttpStatus.OK);
    }

    @PatchMapping("/approval/{seller-id}")
    public ResponseEntity approveSeller(@PathVariable("seller-id")long sellerId){
        Seller updateSeller = adminService.approveSeller(sellerId);
        AdminResponseDto response = mapper.sellerToAdminResponseDto(updateSeller);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PatchMapping("/rejected/{seller-id}")
    public ResponseEntity rejectedSellerStatus(@PathVariable("seller-id") long sellerId) {

        Seller updateSeller = adminService.rejectedSeller(sellerId);
        AdminResponseDto response = mapper.sellerToAdminResponseDto(updateSeller);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
