package com.blend.server.cart;

import com.blend.server.Product.Product;
import com.blend.server.Product.ProductMapper;
import com.blend.server.Product.ProductResponseDto;
import com.blend.server.order.Order;
import com.blend.server.order.OrderCreateDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    default CartResponseDto cartProductsToCartResponseDto (List<CartProduct> cartProductList,long cartId){
        CartResponseDto cartResponseDto = new CartResponseDto();

        List<CartProductResponseDto> cartProductDtoList = cartProductList.stream()
                .map(cartProduct -> {
                    CartProductResponseDto cartProductResponseDto = new CartProductResponseDto();
                    ProductResponseDto productResponseDto = ProductMapper.productToProductResponseDto(cartProduct.getProduct());
                    int productCount = cartProduct.getProductCount();

                    cartProductResponseDto.setProductResponseDto(productResponseDto);
                    cartProductResponseDto.setProductCount(productCount);

                    return cartProductResponseDto;
                }).collect(Collectors.toList());

        cartResponseDto.setCartId(cartId);
        cartResponseDto.setCartProductList(cartProductDtoList);

        return cartResponseDto;
    }

    List<CartResponseDto> cartsToCartResponseDtos(List<CartProduct> carts);
}
