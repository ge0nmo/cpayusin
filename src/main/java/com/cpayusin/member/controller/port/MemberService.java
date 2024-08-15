package com.cpayusin.member.controller.port;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.infrastructure.MemberEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface MemberService
{
    MemberEntity save(MemberEntity memberEntity);

    MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, MemberEntity currentMemberEntity);

    MemberEntity getMemberById(long id);

    MemberDetailResponse getMemberDetailResponse(Long memberId);

    SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable);

    boolean deleteById(MemberEntity memberEntity);

    MemberSingleResponse getMemberSingleResponse(Long memberId);

    MemberEntity findMemberByEmail(String email);

    Optional<MemberEntity> findOptionalMemberByEmail(String email);
}
