package com.cpayusin.common.controller.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Data
@Builder
@NoArgsConstructor
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
