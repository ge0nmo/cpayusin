package com.cpayusin.mapper;

import com.cpayusin.member.infrastructure.Member;
import com.cpayusin.member.controller.request.MemberRegisterRequest;
import com.cpayusin.member.controller.response.MemberCreateResponse;
import com.cpayusin.member.controller.response.MemberDetailResponse;
import com.cpayusin.member.controller.response.MemberUpdateResponse;
import com.cpayusin.member.controller.response.ResetPasswordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberMapper
{
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberCreateResponse toMemberCreateResponse(Member member);

    MemberUpdateResponse toMemberUpdateResponse(Member member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "platform", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isRemoved", ignore = true)
    Member toMemberEntity(MemberRegisterRequest postDto);

    MemberDetailResponse toMemberDetailResponse(Member member);


    ResetPasswordResponse toResetPasswordResponse(Member member);
}
