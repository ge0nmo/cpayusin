package com.jbaacount.payload.request.post;

import com.jbaacount.global.validation.notspace.NotSpace;
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

    private String content;
    private Long boardId;

    @Builder.Default
    private List<String> deletedImg = new ArrayList<>();
}
