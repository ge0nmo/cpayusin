package com.cpayusin.mapper;

import com.cpayusin.model.Member;
import com.cpayusin.payload.request.member.MemberRegisterRequest;
import com.cpayusin.payload.response.member.MemberCreateResponse;
import com.cpayusin.payload.response.member.MemberDetailResponse;
import com.cpayusin.payload.response.member.MemberUpdateResponse;
import com.cpayusin.payload.response.member.ResetPasswordResponse;
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
