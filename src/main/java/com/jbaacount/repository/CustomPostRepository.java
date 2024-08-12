package com.jbaacount.repository;

import com.jbaacount.payload.response.post.PostMultiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomPostRepository
{
    Slice<PostMultiResponse> findAllPostsByBoardIds(List<Long> boardIds, Long lastPost,  Pageable pageable);
}
