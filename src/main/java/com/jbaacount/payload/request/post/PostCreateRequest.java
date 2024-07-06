package com.jbaacount.payload.request.post;

import com.jbaacount.global.validation.notspace.NotSpace;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCreateRequest
{
    @NotSpace
    @Size(max = 50, message = "게시글 제목은 최대 50자까지 입력 가능합니다.")
    private String title;
    private String content;

    private Long boardId;
}
