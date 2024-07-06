package com.jbaacount.service;

import com.jbaacount.global.dto.SliceDto;
import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.mapper.MemberMapper;
import com.jbaacount.model.Member;
import com.jbaacount.payload.request.member.MemberUpdateRequest;
import com.jbaacount.payload.response.member.*;
import com.jbaacount.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jbaacount.service.UtilService.generateRemovedEmail;
import static com.jbaacount.service.UtilService.generateRemovedNickname;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService
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

        if(multipartFile != null && !multipartFile.isEmpty())
        {
            fileService.deleteProfilePhoto(findMember.getId());
            String url = fileService.storeProfileImage(multipartFile, findMember);
            findMember.setUrl(url);
        }

        if(request != null)
        {
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

    public List<MemberScoreResponse> findTop3MembersByScore()
    {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startMonth = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);
        LocalDateTime endMonth = startMonth.plusMonths(1);

        log.info("findTop3Members");
        return memberRepository.memberResponseForReward(startMonth, endMonth);
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
