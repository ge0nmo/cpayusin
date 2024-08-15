package com.cpayusin.comment.controller.request;

import com.cpayusin.common.validation.notspace.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest
{
    @NotSpace
    private String text;
}
