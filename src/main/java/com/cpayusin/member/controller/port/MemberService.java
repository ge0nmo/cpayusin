package com.cpayusin.member.controller.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.infrastructure.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
