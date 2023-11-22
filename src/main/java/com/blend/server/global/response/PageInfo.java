package com.blend.server.global.response;

import lombok.*;


@NoArgsConstructor(access = AccessLevel.NONE) // 기본생성자 생성 막기
@Getter
public class PageInfo {
    private  int page;
    private  int size;
    private  long totalElements;
    private  int totalPages;

    @Builder
    public PageInfo(int page, int size, long totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
