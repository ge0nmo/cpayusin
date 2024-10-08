package com.cpayusin.board.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse
{
    private Long id;

    private String name;

    private Integer orderIndex;

    private Long parentId;
    private Boolean isAdminOnly;
}
