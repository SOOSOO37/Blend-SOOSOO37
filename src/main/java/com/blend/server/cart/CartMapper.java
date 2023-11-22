package com.blend.server.cart;

import com.blend.server.product.ProductMapper;
import com.blend.server.product.ProductResponseDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    default CartResponseDto cartProductsToCartResponseDto (List<CartProduct> cartProductList, Cart cart){
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

        cartResponseDto.setCartId(cart.getId());
        cartResponseDto.setCartProductList(cartProductDtoList);

        return cartResponseDto;
    }

    List<CartResponseDto> cartsToCartResponseDtos(List<CartProduct> carts);
}
