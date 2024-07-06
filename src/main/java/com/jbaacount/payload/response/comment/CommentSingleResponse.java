package com.jbaacount.payload.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class CommentSingleResponse
{
    private Long memberId;
    private String nickname;
    private String memberProfile;

    private Long commentId;
    private Long parentId;
    private String text;
    private int voteCount;
    private boolean voteStatus;
    private Boolean isRemoved;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createdAt;
}
