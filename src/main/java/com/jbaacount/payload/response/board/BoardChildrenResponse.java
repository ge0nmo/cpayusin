package com.jbaacount.payload.response.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardChildrenResponse
{
    private Long id;

    private String name;

    private String type;

    private Integer orderIndex;
    private Boolean isAdminOnly;

    private Long parentId;
}
