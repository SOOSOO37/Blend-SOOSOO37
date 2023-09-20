package com.blend.server.Product.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),

    PRODUCT_EXISTS(409, "이미  존재하는 상품입니다."),

    CATEGORY_NOT_FOUND(404, "해당 카테고리가 존재하지 않습니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}

