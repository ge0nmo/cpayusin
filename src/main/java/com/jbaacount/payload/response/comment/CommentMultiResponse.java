package com.jbaacount.payload.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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
