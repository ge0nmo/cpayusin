package com.cpayusin.comment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatedResponse
{
    private Long id;
    private String text;
    private LocalDateTime createdAt;
}
