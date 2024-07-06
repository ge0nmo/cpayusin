package com.jbaacount.payload.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateResponse
{
    private Long id;
    private String text;
    private LocalDateTime modifiedAt;
}
