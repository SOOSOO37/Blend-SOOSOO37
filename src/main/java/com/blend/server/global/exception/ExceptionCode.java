package com.blend.server.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),

    PRODUCT_EXISTS(409, "이미  존재하는 상품입니다."),

    REVIEW_EXISTS(405,"리뷰가 존재합니다."),

    REVIEW_NOT_FOUND(404,"해당 리뷰가 존재하지 않습니다."),

    ANSWER_NOT_FOUND(404,"해당 답변이 존재하지 않습니다."),

    REVIEW_REMOVED(403,"삭제된 리뷰 입니다."),

    QUANTITY_EXCEEDED(404,"제품 수량을 초과했습니다."),

    CATEGORY_NOT_FOUND(404, "해당 카테고리가 존재하지 않습니다."),

    CATEGORY_EXISTS(409,"이미 존재하는 카테고리 입니다."),

    ORDER_NOT_FOUND(404, "해당 주문이 존재하지 않습니다."),

    DO_NOT_NEXTSTEP(403, "변경 불가능한 진행단계 입니다."),

    CARTPRODUCT_NOT_FOUND(404, "장바구니 상품이 존재하지 않습니다."),

    CART_NOT_FOUND(404, "장바구니가 존재하지 않습니다."),

    CARTPRODUCT_EXISTS(409, "이미 장바구니에 존재하는 상품입니다."),
    IMAGE_NOT_FOUND(404, "해당 이미지가 존재하지 않습니다."),

    USER_EMAIL_EXISTS(409, "이미 등록된 이메일입니다."),

    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),

    SELLER_NOT_FOUND(404, "존재하지 않는 판매자 입니다."),

    ADMIN_NOT_FOUND(404, "존재하지 않는 관리자 입니다."),

    SELLER_WAIT(404, "가입 대기 중인 판매자입니다."),

    SELLER_NOT_ALLOWED(409, "권한이 없는 판매자입니다"),
    SELLER_REJECTED(404, "가입 거절된 판매자입니다."),

    USER_QUIT(404, "탈퇴한 회원입니다."),

    SLEEPER_ACCOUNT(404, "휴면계정 입니다."),

    USER_NICKNAME_EXISTS(409, "이미 등록된 닉네임입니다."),

    REGNUMBER_EXISTS(409, "이미  존재하는 사업자등록번호 입니다.");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}

