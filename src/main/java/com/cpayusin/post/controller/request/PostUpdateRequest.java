package com.cpayusin.post.controller.request;

import com.cpayusin.common.validation.notspace.NotSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class PostUpdateRequest
{
    @NotSpace
    @Size(max = 50, message = "게시글 제목은 최대 50자까지 입력 가능합니다.")
    private String title;

    @NotBlank
    private String content;
}
