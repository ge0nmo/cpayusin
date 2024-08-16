package com.cpayusin.member.repository;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository
{
    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

}
