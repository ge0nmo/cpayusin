package com.jbaacount.model.type;

import lombok.Getter;

@Getter
public enum CommentType
{
    PARENT_COMMENT("댓글"),

    CHILD_COMMENT("대댓글");

    private String code;

    CommentType(String code)
    {
        this.code = code;
    }
}
