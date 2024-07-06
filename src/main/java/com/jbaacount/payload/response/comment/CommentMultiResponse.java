package com.jbaacount.payload.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentMultiResponse
{
    private Long id;
    private String text;
    private Integer voteCount;
    private Boolean voteStatus;
    private Boolean isRemoved;

    private Long memberId;
    private String memberName;

    private String memberProfile;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createdAt;

    @Builder.Default
    private List<CommentChildrenResponse> children = new ArrayList<>();

}
