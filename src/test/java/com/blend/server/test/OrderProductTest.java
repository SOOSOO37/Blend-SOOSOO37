package com.blend.server.test;

import com.blend.server.category.CategoryRepository;
import com.blend.server.global.exception.BusinessLogicException;
import com.blend.server.order.OrderCountUpdateDto;
import com.blend.server.orderproduct.OrderProduct;
import com.blend.server.orderproduct.OrderProductRepository;
import com.blend.server.orderproduct.OrderProductService;
import com.blend.server.orderproduct.OrderProductUpdateDto;
import com.blend.server.product.Product;
import com.blend.server.product.ProductRepository;
import com.blend.server.product.ProductService;
import com.blend.server.seller.Seller;
import com.blend.server.seller.SellerRepository;
import com.blend.server.seller.SellerService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderProductTest {

    @InjectMocks
    private OrderProductService orderProductService;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private SellerService sellerService;

    @Mock
    private SellerRepository sellerRepository;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("주문상품 상태 업데이트 테스트")
    @Test
    public void updateOrderStatusTest(){
        OrderProductUpdateDto orderProductUpdateDto= new OrderProductUpdateDto();
        orderProductUpdateDto.setOrderProductStatus(OrderProduct.OrderProductStatus.PAY_FINISH);

        Seller seller = new Seller();
        seller.setId(1L);

        Product product = TestObjectFactory.createProduct();
        product.setSeller(seller);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setProduct(product);
        orderProduct.setOrderProductStatus(OrderProduct.OrderProductStatus.PAY_STANDBY);

        when(orderProductRepository.findById(orderProduct.getOrderProductId())).thenReturn(Optional.of(orderProduct));
        when(orderProductRepository.save(any(OrderProduct.class))).thenReturn(orderProduct);
        OrderProduct result = orderProductService.updateOrderStatus(orderProduct.getOrderProductId(),orderProductUpdateDto,seller);

        assertNotNull(result);
        assertEquals(orderProduct.getOrderProductId(), result.getOrderProductId());
        assertEquals(orderProductUpdateDto.getOrderProductStatus(), result.getOrderProductStatus());
    }

    @DisplayName("판매자의 주문상품 조회")
    @Test
    public void findAllOrderProductBySellerTest (){
        Seller seller = new Seller();
        seller.setId(1L);

        Product product = TestObjectFactory.createProduct();
        product.setSeller(seller);

        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProductList.add(orderProduct);

        when(sellerService.findVerifiedSeller(seller.getId())).thenReturn(seller);
        when(orderProductRepository.findByProductSeller(seller,
                PageRequest.of(0,10, Sort.by("order.createdAt").descending())))
                .thenReturn(new PageImpl<>(orderProductList));

        Page<OrderProduct> result = orderProductService.findAllOrderProductBySeller(0,10,seller);

        assertNotNull(result);
        assertEquals(orderProductList.size(),result.getContent().size());
    }

    @DisplayName("운송장 번호 등록 테스트")
    @Test
    public void postTrackingNumberTest() {


        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);
        orderProduct.setOrderProductStatus(OrderProduct.OrderProductStatus.DELIVERY_PROCESS);

        String trackingNumber = "1234567890";

        when(orderProductRepository.save(any(OrderProduct.class))).thenReturn(orderProduct);

        orderProductService.postTrackingNumber(orderProduct, trackingNumber);

        assertEquals(trackingNumber, orderProduct.getTrackingNumber());
    }

    @DisplayName("주문 상품 존재 여부 확인")
    @Test
    public void findVerifiedOrderProductExistingTest() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(1L);

        when(orderProductRepository.findById(orderProduct.getOrderProductId())).thenReturn(Optional.of(orderProduct));

        OrderProduct result = orderProductService.findVerifiedOrderProduct(orderProduct.getOrderProductId());

        assertNotNull(result);

    }


}
