package com.cpayusin.member.repository;

import com.cpayusin.common.controller.response.SliceDto;
import com.cpayusin.common.utils.PaginationUtils;
import com.cpayusin.member.controller.response.MemberMultiResponse;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.cpayusin.member.domain.QMember.member;


@Slf4j
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository
{
    private final JPAQueryFactory query;
    private final PaginationUtils paginationUtils;

    @Override
    public SliceDto<MemberMultiResponse> findAllMembers(String keyword, Long memberId, Pageable pageable)
    {
        log.info("===findAllMembers in repository===");
        List<MemberMultiResponse> memberDto = query
                .select(getMemberList())
                .from(member)
                .where(ltMemberId(memberId))
                .where(checkEmailKeyword(keyword))
                .where(checkNicknameKeyword(keyword))
                .where(member.isRemoved.isFalse())
                .orderBy(member.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        log.info("list size = {}", memberDto.size());

        Slice<MemberMultiResponse> slice = paginationUtils.toSlice(pageable, memberDto);

        return new SliceDto<>(memberDto, slice);
    }

    private ConstructorExpression<MemberMultiResponse> getMemberList()
    {
        log.info("===memberToResponse===");
        return Projections.constructor(MemberMultiResponse.class,
                member.id,
                member.nickname,
                member.email,
                member.url != null ? member.url : null,
                member.role);
    }


    private BooleanExpression ltMemberId(Long memberId)
    {
        return memberId != null ? member.id.lt(memberId) : null;
    }


    private BooleanExpression checkEmailKeyword(String keyword)
    {
         return keyword != null ? member.email.lower().contains(keyword.toLowerCase()) : null;
    }

    private BooleanExpression checkNicknameKeyword(String keyword)
    {
        return keyword != null ? member.nickname.lower().contains(keyword.toLowerCase()) : null;
    }

}
