package com.cpayusin.member.service;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.member.mapper.MemberMapper;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.domain.Member;
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
    public Member save(Member member)
    {
        return memberRepository.save(member);
    }

    @Transactional
    public MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, Member currentMember)
    {
        Member findMember = getMemberById(currentMember.getId());
        log.info("===updateMember===");
        log.info("findMember email = {}", findMember.getEmail());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileService.deleteProfilePhoto(findMember.getId());
            String url = fileService.storeProfileImage(multipartFile, findMember);
            findMember.setUrl(url);
        }
        if (request != null) {
            Optional.ofNullable(request.getNickname())
                    .ifPresent(findMember::updateNickname);
            Optional.ofNullable(request.getPassword())
                    .ifPresent(password -> findMember.updatePassword(passwordEncoder.encode(password)));
        }
        return MemberMapper.INSTANCE.toMemberUpdateResponse(findMember);
    }

    public Member getMemberById(long id)
    {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    public MemberDetailResponse getMemberDetailResponse(Long memberId)
    {
        Member member = getMemberById(memberId);
        return MemberMapper.INSTANCE.toMemberDetailResponse(member);
    }

    public SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        return memberRepository.findAllMembers(keyword, memberId, pageable);
    }


    @Transactional
    public boolean deleteById(Member member)
    {
        member.setEmail(generateRemovedEmail());
        member.setNickname(generateRemovedNickname());
        member.setRemoved(true);
        log.info("member email = {}, member nickname = {}", member.getEmail(), member.getNickname());
        memberRepository.save(member);
        return member.isRemoved();
    }

    public MemberSingleResponse getMemberSingleResponse(Long memberId)
    {
        return memberRepository.findSingleResponseById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }


    public Member findMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    public Optional<Member> findOptionalMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email);
    }

}
