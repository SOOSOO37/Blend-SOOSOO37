package com.blend.server.test;

import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.global.exception.ExceptionCode;
import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.product.ProductService;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.seller.SellerService;
import com.blend.server.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


public class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomAuthorityUtils customAuthorityUtils;

    @BeforeEach
    void setUp() {
        // 모의 객체를 초기화하고 의존성 주입 (현재 테스트 클래스의 인스턴스를 초기화)
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("판매자 등록 테스트")
    @Test
    public void createUserTest(){

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setEmail("seller@gmail.com");
        seller.setPassword("qwer1234!");
        seller.setRegNumber("1234567890");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(sellerRepository.findByRegNumber(anyString())).thenReturn(Optional.empty());
        when(customAuthorityUtils.createSellerRoles(any(Seller.class))).thenReturn(List.of("SELLER"));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller createdSeller = sellerService.createSeller(seller);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(sellerRepository, times(1)).findByRegNumber(anyString());
        verify(customAuthorityUtils, times(1)).createSellerRoles(any(Seller.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(sellerRepository, times(1)).save(any(Seller.class));

        assertNotNull(createdSeller);
        assertEquals(createdSeller.getId(),seller.getId());
        assertEquals(createdSeller.getEmail(),seller.getEmail());
    }

    @DisplayName("판매자 업데이트 테스트 - 승인된 판매자")
    @Test
    public void testUpdateSeller() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("기존 이름");
        seller.setAddress("기존 주소");
        seller.setPhone("기존 번호");
        seller.setSellerStatus(Seller.SellerStatus.SELLER_APPROVE);

        when(sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));

        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Seller updatedSeller = new Seller();
        updatedSeller.setId(1L);
        updatedSeller.setName("새로운 이름");
        updatedSeller.setAddress("새로운 주소");
        updatedSeller.setPhone("새로운 번호");

        Seller result = sellerService.updateSeller(updatedSeller);

        assertNotNull(result);
        assertEquals(seller.getId(), result.getId());
        assertEquals("새로운 이름", result.getName());
        assertEquals("새로운 주소", result.getAddress());
        assertEquals("새로운 번호", result.getPhone());
    }

    @DisplayName("판매자 업데이트 테스트 - 대기중인 판매자")
    @Test
    public void testUpdateWaitSeller() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("기존 이름");
        seller.setAddress("기존 주소");
        seller.setPhone("기존 번호");;
        seller.setSellerStatus(Seller.SellerStatus.SELLER_WAIT);

        when(sellerRepository.findById(seller.getId())).thenReturn(Optional.of(seller));

        Seller updatedSeller = new Seller();
        updatedSeller.setId(1L);
        updatedSeller.setName("새로운 이름");
        updatedSeller.setAddress("새로운 주소");
        updatedSeller.setPhone("새로운 번호");

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            sellerService.updateSeller(updatedSeller);
        });

        assertEquals("가입 대기 중인 판매자입니다.", exception.getMessage());
    }
    @DisplayName("판매자 판매중인 상품 조회 테스트")
    @Test
    void findProductsTest() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerStatus(Seller.SellerStatus.SELLER_APPROVE);

        List<Product> products = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId(i);
            product.setProductStatus(Product.ProductStatus.SALE);
            product.setSeller(seller);
            products.add(product);
        }

        seller.setProduct(products);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findAllBySellerAndProductStatus(eq(seller), eq(Product.ProductStatus.SALE), any()))
                .thenReturn(new PageImpl<>(products));

        Page<Product> result = sellerService.findProducts(5, 1, seller);

        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
    }
    @DisplayName("판매자 상품 삭제 테스트")
    @Test
    void deleteProductTest() {
        // 가상의 Seller 객체 생성
        Seller seller = new Seller();
        seller.setId(1L);

        // 가상의 Product 객체 생성
        Product product = new Product();
        product.setId(1L);
        product.setProductStatus(Product.ProductStatus.SALE);
        product.setSeller(seller);

        // ProductService 내부에서 호출되는 메소드의 Mock 설정
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        // ProductService의 deleteProduct 메소드 호출
        sellerService.deleteProduct(1L, seller);

        // 결과 검증
        assertEquals(Product.ProductStatus.PRODUCT_DELETE, product.getProductStatus());
        verify(productRepository, times(1)).save(product);
    }
}
