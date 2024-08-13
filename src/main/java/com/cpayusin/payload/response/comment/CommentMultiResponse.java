package com.cpayusin.payload.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentMultiResponse
{
    private Long boardId;
    private String boardName;
    private Long postId;
    private String postTitle;

    private List<CommentResponse> comments;

}
