package com.jbaacount.payload.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostMultiResponse
{
    private Long memberId;
    private String memberName;

    private Long boardId;
    private String boardName;


    private Long postId;
    private String title;
    private String content;
    private Integer voteCount;

    private Integer commentsCount;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createdAt;
}
