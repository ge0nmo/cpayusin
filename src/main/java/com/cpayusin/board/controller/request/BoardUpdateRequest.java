package com.cpayusin.board.controller.request;

import com.cpayusin.common.validation.notspace.NotSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateRequest
{
    @NotNull
    private Long id;

    @NotSpace
    @Size(max = 15, message = "게시판 제목은 최대 15자까지 입력 가능합니다.")
    private String name;

    private Boolean isAdminOnly;

    private Boolean isDeleted;

    @NotNull
    private Integer orderIndex;

    @Builder.Default
    private List<CategoryUpdateRequest> category = new ArrayList<>();
}
