package com.jbaacount.payload.response.post;

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
    int getCommentsCount();
    LocalDateTime getCreatedAt();
}
