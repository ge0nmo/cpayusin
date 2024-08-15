package com.cpayusin.member.infrastructure;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
import com.cpayusin.member.domain.MemberDomain;
import com.cpayusin.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository
{
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<MemberDomain> findByEmail(String email)
    {
        return memberJpaRepository.findByEmail(email).map(Member::toModel);
    }

    @Override
    public Optional<MemberDomain> findByNickname(String nickname)
    {
        return memberJpaRepository.findByNickname(nickname).map(Member::toModel);
    }

    @Override
    public boolean existsByEmail(String email)
    {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname)
    {
        return memberJpaRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<MemberSingleResponse> findSingleResponseById(Long memberId)
    {
        return memberJpaRepository.findSingleResponseById(memberId);
    }

    @Override
    public Optional<MemberDomain> findById(Long memberId)
    {
        return memberJpaRepository.findById(memberId).map(Member::toModel);
    }

    @Override
    public MemberDomain save(MemberDomain memberDomain)
    {
        return memberJpaRepository.save(Member.from(memberDomain)).toModel();
    }

    @Override
    public SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        return memberJpaRepository.findAllMembers(keyword, memberId, pageable);
    }

    @Override
    public List<MemberDomain> saveAll(List<MemberDomain> memberDomains)
    {
        List<Member> savedMembers = memberJpaRepository.saveAll(memberDomains.stream()
                .map(Member::from)
                .toList());

        return savedMembers.stream()
                .map(Member::toModel)
                .toList();
    }
}
