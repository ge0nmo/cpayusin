package com.cpayusin.post.controller.response;

import java.time.LocalDateTime;

public interface PostResponseProjection
{
    long getMemberId();
    String getMemberName();
    long getBoardId();
    String getBoardName();
    long getPostId();
    String getTitle();
    int getVoteCount();
    int getCommentCount();
    LocalDateTime getCreatedAt();
}
