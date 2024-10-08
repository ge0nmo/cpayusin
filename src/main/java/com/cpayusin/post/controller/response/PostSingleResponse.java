package com.cpayusin.post.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostSingleResponse
{
    private Long memberId;
    private String nickname;
    private Long boardId;
    private String boardName;

    @JsonProperty("postId")
    private Long id;
    private String title;
    private String content;

    private Integer voteCount;
    private boolean voteStatus;

    private LocalDateTime createdAt;
}
