package com.cpayusin.payload.request.comment;

import com.cpayusin.global.validation.notspace.NotSpace;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CommentCreateRequest
{
    @NotSpace
    private String text;

    private Long postId;

    private Long parentCommentId;
}
