package com.blend.server.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.NONE) // 기본생성자 생성 막기
@Getter
public class PageInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
