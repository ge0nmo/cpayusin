package com.cpayusin.comment.controller.response;

import com.cpayusin.comment.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentChildrenResponse
{
    private Long id;
    private String text;
    private Boolean isRemoved;

    private Long memberId;
    private String memberName;
    private String memberProfile;

    private Long parentId;

    private LocalDateTime createdAt;


    public static CommentChildrenResponse from(Comment comment)
    {
        return CommentChildrenResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .isRemoved(comment.getIsRemoved())
                .memberId(comment.getMember().getId())
                .memberName(comment.getMember().getNickname())
                .memberProfile(comment.getMember().getUrl())
                .parentId(comment.getParent().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
