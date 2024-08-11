package com.jbaacount.payload.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Builder
@Data
@AllArgsConstructor
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
}
