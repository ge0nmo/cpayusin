package com.cpayusin.member.service.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.infrastructure.MemberEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository
{
    Optional<MemberEntity> findByEmail(@Param("email") String email);

    Optional<MemberEntity> findByNickname(@Param("nickname") String nickname);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    Optional<MemberSingleResponse> findSingleResponseById(@Param("memberId") Long memberId);

    Optional<MemberEntity> findById(@Param("memberId") Long memberId);

    MemberEntity save(MemberEntity member);

    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

    List<MemberEntity> saveAll(List<MemberEntity> members);
}
