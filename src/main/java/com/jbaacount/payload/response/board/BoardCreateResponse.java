package com.jbaacount.payload.response.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateResponse
{
    private Long id;

    private String name;

    private Integer orderIndex;

    private Long parentId;
    private Boolean isAdminOnly;
}
