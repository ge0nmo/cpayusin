package com.jbaacount.global.exception;

import lombok.Getter;

public enum ExceptionMessage
{
    /**
     * member
     */
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXIST("이미 사용중인 이메일입니다."),
    NICKNAME_ALREADY_EXIST("이미 사용중인 닉네임입니다."),

    MEMBER_UNAUTHORIZED("권한이 없습니다."),

    INVALID_VERIFICATION_CODE("인증코드가 일치하지 않습니다."),

    EXPIRED_VERIFICATION_CODE("만료된 인증코드입니다."),
    /**
     * token
     */

    EXPIRED_TOKEN("이미 만료된 토큰입니다."),
    TOKEN_NOT_VALID("유효하지 않은 토큰입니다."),

    UNSUPPORTED_TOKEN("지원되지 않는 형식의 토큰입니다."),

    CLAIM_EMPTY("토큰의 정보를 찾을 수 없습니다."),

    INVALID_TOKEN_SIGNATURE("토큰의 서명이 유효하지 않습니다."),
    TOKEN_NOT_FOUND("해당 리프레시 토큰을 찾을 수 없습니다."),


    /**
     * post
     */

    POST_NOT_FOUND("해당 게시물을 찾을 수 없습니다."),


    /**
     * category
     */
    CATEGORY_NOT_FOUND("해당 카테고리를 찾을 수 없습니다."),

    /**
     * comment
     */

    COMMENT_NOT_FOUND("해당 댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_NESTED("댓글을 2단계 이상 저장할 수 없습니다."),

    /**
     * board
     */
    BOARD_TYPE_ERROR("하위 게시판은 한 개만 생성할 수 있습니다."),

    BOARD_NOT_FOUND("존재하지 않는 게시판입니다."),

    /**
     * mail
     */
    MAIL_ERROR("메일 전송에 실패했습니다."),


    /**
     * file
     */
    FILE_NOT_STORED("파일을 저장할 수 없습니다."),

    EXT_NOT_ACCEPTED("이미지 확장자만 사용할 수 있습니다.");


    @Getter
    private final String message;


    ExceptionMessage(String message)
    {
        this.message = message;
    }

    public String get()
    {
        return message;
    }
}
