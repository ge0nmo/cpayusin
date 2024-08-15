package com.cpayusin.member.service.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.domain.MemberDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository
{
    Optional<MemberDomain> findByEmail(@Param("email") String email);

    Optional<MemberDomain> findByNickname(@Param("nickname") String nickname);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    Optional<MemberSingleResponse> findSingleResponseById(@Param("memberId") Long memberId);

    Optional<MemberDomain> findById(@Param("memberId") Long memberId);

    MemberDomain save(MemberDomain member);

    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

    List<MemberDomain> saveAll(List<MemberDomain> members);
}
