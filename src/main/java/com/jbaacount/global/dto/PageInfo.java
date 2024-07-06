package com.jbaacount.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Data
@Builder
@AllArgsConstructor
public class PageInfo
{
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static PageInfo of(Page<?> page)
    {
        return PageInfo.builder()
                .page(page.getPageable().getPageNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
