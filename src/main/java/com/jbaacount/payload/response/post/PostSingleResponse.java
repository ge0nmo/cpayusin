package com.jbaacount.payload.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jbaacount.payload.response.FileResponse;
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
    private Long boardId;
    private String nickname;

    @JsonProperty("postId")
    private Long id;
    private String title;
    private String content;
    private List<String> files;

    private Integer voteCount;
    private boolean voteStatus;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createdAt;
}
