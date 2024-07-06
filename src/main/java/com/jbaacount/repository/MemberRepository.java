package com.jbaacount.repository;

import com.jbaacount.model.Member;
import com.jbaacount.payload.response.member.MemberScoreResponse;
import com.jbaacount.payload.response.member.MemberSingleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom
{
    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isRemoved = FALSE ")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m where REPLACE(lower(m.nickname), ' ', '') = REPLACE(lower(:nickname), ' ', '')")
    Optional<Member> findByNickname(@Param("nickname") String nickname);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("SELECT new com.jbaacount.payload.response.member.MemberSingleResponse(m.id, m.nickname, m.url, m.role) FROM Member m " +
            "WHERE m.id = :memberId AND m.isRemoved = FALSE ")
    Optional<MemberSingleResponse> findSingleResponseById(@Param("memberId") Long memberId);

    @Query("SELECT new com.jbaacount.payload.response.member.MemberScoreResponse(m.id, m.nickname, m.score) FROM Member m " +
            "JOIN Post p ON p.member.id = m.id " +
            "WHERE m.isRemoved = FALSE AND m.role != 'ADMIN' " +
            "ORDER BY m.score LIMIT 3")
    List<MemberScoreResponse> memberResponseForReward(LocalDateTime startMonth, LocalDateTime endMonth);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.isRemoved = FALSE ")
    Optional<Member> findById(@Param("memberId") Long memberId);
}
