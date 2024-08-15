package com.cpayusin.member.infrastructure;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.cpayusin.member.controller.response.MemberSingleResponse;
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
    public Optional<Member> findByEmail(String email)
    {
        return memberJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<Member> findByNickname(String nickname)
    {
        return memberJpaRepository.findByNickname(nickname);
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
    public Optional<Member> findById(Long memberId)
    {
        return memberJpaRepository.findById(memberId);
    }

    @Override
    public Member save(Member member)
    {
        return memberJpaRepository.save(member);
    }

    @Override
    public SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        return memberJpaRepository.findAllMembers(keyword, memberId, pageable);
    }

    @Override
    public List<Member> saveAll(List<Member> members)
    {
        return memberJpaRepository.saveAll(members);
    }
}
