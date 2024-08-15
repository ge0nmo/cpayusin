package com.cpayusin.post.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Builder
@Data
@NoArgsConstructor
public class PostMultiResponse
{
    private long memberId;
    private String memberName;

    private long boardId;
    private String boardName;


    private long postId;
    private String title;
    private int voteCount;

    private int commentsCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @QueryProjection
    public PostMultiResponse(long memberId, String memberName, long boardId, String boardName, long postId, String title, int voteCount, int commentsCount, LocalDateTime createdAt)
    {
        this.memberId = memberId;
        this.memberName = memberName;
        this.boardId = boardId;
        this.boardName = boardName;
        this.postId = postId;
        this.title = title;
        this.voteCount = voteCount;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
    }
}
