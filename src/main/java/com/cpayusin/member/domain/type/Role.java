package com.cpayusin.member.domain.type;

import lombok.Getter;

@Getter
public enum Role
{
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value)
    {
        this.value = value;
    }
}
