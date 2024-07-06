package com.jbaacount.model.type;

import lombok.Getter;

@Getter
public enum Platform
{
    HOME("home"),
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao"),
    FACEBOOK("facebook");

    private final String value;

    Platform(String value)
    {
        this.value = value;
    }

    public static Platform platformValue(String registrationId)
    {
        for (Platform platform : Platform.values())
        {
            if(registrationId.equals(platform.getValue()))
                return platform;
        }
        return null;
    }

}
