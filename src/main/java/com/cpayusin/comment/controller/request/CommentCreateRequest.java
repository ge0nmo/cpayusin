package com.cpayusin.comment.controller.request;

import com.cpayusin.common.validation.notspace.NotSpace;
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
