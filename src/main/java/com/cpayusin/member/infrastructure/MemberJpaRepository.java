package com.cpayusin.member.infrastructure;

import com.cpayusin.member.controller.response.MemberSingleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long>, CustomMemberRepository
{
    @Query("SELECT m FROM MemberEntity m WHERE m.email = :email AND m.isRemoved = FALSE ")
    Optional<MemberEntity> findByEmail(@Param("email") String email);

    @Query("select m from MemberEntity m where REPLACE(lower(m.nickname), ' ', '') = REPLACE(lower(:nickname), ' ', '')")
    Optional<MemberEntity> findByNickname(@Param("nickname") String nickname);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("SELECT new com.cpayusin.member.controller.response.MemberSingleResponse(m.id, m.nickname, m.url, m.role) FROM MemberEntity m " +
            "WHERE m.id = :memberId AND m.isRemoved = FALSE ")
    Optional<MemberSingleResponse> findSingleResponseById(@Param("memberId") Long memberId);

    @Query("SELECT m FROM MemberEntity m WHERE m.id = :memberId AND m.isRemoved = FALSE ")
    Optional<MemberEntity> findById(@Param("memberId") Long memberId);
}
