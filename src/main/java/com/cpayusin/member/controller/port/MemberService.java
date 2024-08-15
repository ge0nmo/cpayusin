package com.cpayusin.member.controller.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface MemberService
{
    MemberDomain save(MemberDomain member);

    MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, Member currentMember);

    MemberDomain getMemberById(long id);

    MemberDetailResponse getMemberDetailResponse(Long memberId);

    SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable);

    boolean deleteById(MemberDomain member);

    MemberSingleResponse getMemberSingleResponse(Long memberId);

    MemberDomain findMemberByEmail(String email);

    Optional<MemberDomain> findOptionalMemberByEmail(String email);
}
