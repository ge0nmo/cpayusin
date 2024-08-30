package com.cpayusin.comment.controller.response;

import com.cpayusin.comment.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse
{
    private Long id;
    private String text;
    private Boolean isRemoved;

    private Long memberId;
    private String memberName;

    private String memberProfile;

    private LocalDateTime createdAt;

    @Builder.Default
    private List<CommentChildrenResponse> children = new ArrayList<>();

    public static CommentResponse from(Comment comment)
    {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.id = comment.getId();
        commentResponse.text = comment.getText();
        commentResponse.isRemoved = comment.getIsRemoved();
        commentResponse.memberId = comment.getMember().getId();
        commentResponse.memberName = comment.getMember().getNickname();
        commentResponse.memberProfile = comment.getMember().getUrl();
        commentResponse.createdAt = comment.getCreatedAt();

        return commentResponse;
    }
}
