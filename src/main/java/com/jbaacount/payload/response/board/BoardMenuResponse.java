package com.jbaacount.payload.response.board;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardMenuResponse
{
    private Long id;

    private String name;

    private String type;

    private Integer orderIndex;
    private Boolean isAdminOnly;

    @Builder.Default
    private List<BoardChildrenResponse> category = new ArrayList<>();
}
