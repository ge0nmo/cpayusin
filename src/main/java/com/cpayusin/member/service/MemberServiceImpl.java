package com.cpayusin.member.service;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.cpayusin.common.service.UtilService.generateRemovedEmail;
import static com.cpayusin.common.service.UtilService.generateRemovedNickname;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService
{
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;


    @Transactional
    @Override
    public MemberDomain save(MemberDomain member)
    {
        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, Member currentMember)
    {
        MemberDomain memberDomain = getMemberById(currentMember.getId());
        String url = null;

        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileService.deleteProfilePhoto(memberDomain.getId());
            url = fileService.storeProfileImage(multipartFile, memberDomain);
        }

        if (request != null)
           memberDomain = memberDomain.update(request, url, passwordEncoder);

        return MemberUpdateResponse.from(memberDomain);
    }

    @Override
    public MemberDomain getMemberById(long id)
    {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    @Override
    public MemberDetailResponse getMemberDetailResponse(Long memberId)
    {
        MemberDomain memberDomain = getMemberById(memberId);

        return MemberDetailResponse.from(memberDomain);
    }

    @Override
    public SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        return memberRepository.findAllMembers(keyword, memberId, pageable);
    }


    @Transactional
    @Override
    public boolean deleteById(MemberDomain memberDomain)
    {
        memberDomain = memberDomain.removeMemberLogic();
        memberDomain = memberRepository.save(memberDomain);
        return memberDomain.isRemoved();
    }

    @Override
    public MemberSingleResponse getMemberSingleResponse(Long memberId)
    {
        return memberRepository.findSingleResponseById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    @Override
    public MemberDomain findMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    @Override
    public Optional<MemberDomain> findOptionalMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email);
    }

}
