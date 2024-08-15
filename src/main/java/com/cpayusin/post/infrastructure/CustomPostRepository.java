package com.cpayusin.post.infrastructure;

import com.cpayusin.post.controller.response.PostMultiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomPostRepository
{
    Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost,  Pageable pageable);
}
