package com.cpayusin.post.controller.response;

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
public class PostUpdateResponse
{
    private Long id;
    private String title;
    private String content;

    private LocalDateTime createdAt;

}
