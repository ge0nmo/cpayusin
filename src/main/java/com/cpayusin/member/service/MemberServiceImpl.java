package com.cpayusin.member.service;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.file.controller.port.FileService;
import com.cpayusin.mapper.MemberMapper;
import com.cpayusin.member.controller.port.MemberService;
import com.cpayusin.member.controller.request.MemberUpdateRequest;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.infrastructure.MemberEntity;
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
    public MemberEntity save(MemberEntity memberEntity)
    {
        return memberRepository.save(memberEntity);
    }

    @Transactional
    public MemberUpdateResponse updateMember(MemberUpdateRequest request, MultipartFile multipartFile, MemberEntity currentMemberEntity)
    {
        MemberEntity findMemberEntity = getMemberById(currentMemberEntity.getId());
        log.info("===updateMember===");
        log.info("findMemberEntity email = {}", findMemberEntity.getEmail());
        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileService.deleteProfilePhoto(findMemberEntity.getId());
            String url = fileService.storeProfileImage(multipartFile, findMemberEntity);
            findMemberEntity.setUrl(url);
        }
        if (request != null) {
            Optional.ofNullable(request.getNickname())
                    .ifPresent(findMemberEntity::updateNickname);
            Optional.ofNullable(request.getPassword())
                    .ifPresent(password -> findMemberEntity.updatePassword(passwordEncoder.encode(password)));
        }
        return MemberMapper.INSTANCE.toMemberUpdateResponse(findMemberEntity);
    }

    public MemberEntity getMemberById(long id)
    {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    public MemberDetailResponse getMemberDetailResponse(Long memberId)
    {
        MemberEntity memberEntity = getMemberById(memberId);
        return MemberMapper.INSTANCE.toMemberDetailResponse(memberEntity);
    }

    public SliceDto<MemberMultiResponse> getAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        return memberRepository.findAllMembers(keyword, memberId, pageable);
    }


    @Transactional
    public boolean deleteById(MemberEntity memberEntity)
    {
        memberEntity.setEmail(generateRemovedEmail());
        memberEntity.setNickname(generateRemovedNickname());
        memberEntity.setRemoved(true);
        log.info("memberEntity email = {}, memberEntity nickname = {}", memberEntity.getEmail(), memberEntity.getNickname());
        memberRepository.save(memberEntity);
        return memberEntity.isRemoved();
    }

    public MemberSingleResponse getMemberSingleResponse(Long memberId)
    {
        return memberRepository.findSingleResponseById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }


    public MemberEntity findMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionMessage.USER_NOT_FOUND));
    }

    public Optional<MemberEntity> findOptionalMemberByEmail(String email)
    {
        return memberRepository.findByEmail(email);
    }

}
