package com.jbaacount.payload.request.board;

import com.jbaacount.global.validation.notspace.NotSpace;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardCreateRequest
{
    @NotSpace
    @Size(max = 15, message = "게시판 제목은 최대 15자까지 입력 가능합니다.")
    private String name;

    private Boolean isAdminOnly;

    private Long parentId;
}
