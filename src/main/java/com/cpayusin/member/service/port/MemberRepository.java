package com.cpayusin.member.service.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository
{
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<MemberSingleResponse> findSingleResponseById(Long memberId);

    Optional<Member> findById(Long memberId);

    Member save(Member member);

    SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable);

    List<Member> saveAll(List<Member> members);

    boolean existsByNickname(long memberId, String nickname);
}
