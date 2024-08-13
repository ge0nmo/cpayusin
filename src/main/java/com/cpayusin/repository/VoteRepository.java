package com.cpayusin.repository;

import com.cpayusin.model.Comment;
import com.cpayusin.model.Member;
import com.cpayusin.model.Post;
import com.cpayusin.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long>
{
    @Query("select v from Vote v where v.member = :member and v.post = :post")
    Optional<Vote> findByMemberAndPost(@Param("member") Member member, @Param("post") Post post);

    @Query("select v from Vote v where v.member = :member and v.comment = :comment")
    Optional<Vote> findByMemberAndComment(@Param("member") Member member, @Param("comment") Comment comment);

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.post.id = :postId")
    Optional<Vote> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.comment.id = :commentId")
    Optional<Vote> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId")
    List<Vote> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT v FROM Vote v WHERE v.comment.id = :commentId")
    List<Vote> findAllByCommentId(@Param("commentId") Long commentId);


    boolean existsVoteByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    boolean existsVoteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

}
