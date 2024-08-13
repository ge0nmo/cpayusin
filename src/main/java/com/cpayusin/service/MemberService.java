package com.cpayusin.service;

import com.cpayusin.global.dto.SliceDto;
import com.cpayusin.model.Member;
import com.cpayusin.payload.request.member.MemberUpdateRequest;
import com.cpayusin.payload.response.member.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MemberService
{
    Member save(Member member);

    MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, Member currentMember);

    Member getMemberById(long id);

    MemberDetailResponse getMemberDetailResponse(Long memberId);

    SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable);

    boolean deleteById(Member member);

    MemberSingleResponse getMemberSingleResponse(Long memberId);

    Member findMemberByEmail(String email);

    Optional<Member> findOptionalMemberByEmail(String email);
}
