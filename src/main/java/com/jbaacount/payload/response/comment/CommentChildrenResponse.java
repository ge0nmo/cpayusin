package com.jbaacount.payload.response.comment;

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
    private Integer voteCount;
    private Boolean voteStatus;
    private Boolean isRemoved;

    private Long memberId;
    private String memberName;
    private String memberProfile;

    private Long parentId;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createdAt;
}
