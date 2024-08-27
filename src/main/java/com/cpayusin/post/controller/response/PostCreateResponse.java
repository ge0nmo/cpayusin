package com.cpayusin.post.controller.response;

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
public class PostCreateResponse
{
    private Long id;
    private String title;
    private String content;

    @Builder.Default
    private List<String> files = new ArrayList<>();

    private LocalDateTime createdAt;

}
