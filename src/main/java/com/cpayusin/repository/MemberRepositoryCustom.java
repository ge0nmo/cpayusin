package com.cpayusin.repository;

import com.cpayusin.global.dto.SliceDto;
import com.cpayusin.payload.response.member.MemberMultiResponse;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom
{
    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

}
