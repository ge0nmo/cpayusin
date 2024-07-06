package com.jbaacount.repository;

import com.jbaacount.global.dto.SliceDto;
import com.jbaacount.payload.response.member.MemberDetailResponse;
import com.jbaacount.payload.response.member.MemberMultiResponse;
import com.jbaacount.payload.response.member.MemberScoreResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepositoryCustom
{
    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

}
