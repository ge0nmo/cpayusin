package com.cpayusin.common.security.userdetails;

import com.cpayusin.member.infrastructure.MemberEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class MemberDetails implements UserDetails
{
    private final MemberEntity memberEntity;

    public MemberDetails(MemberEntity memberEntity)
    {
        this.memberEntity = memberEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + memberEntity.getRole());

        return authorities;
    }

    @Override
    public String getPassword()
    {
        return memberEntity.getPassword();
    }

    @Override
    public String getUsername()
    {
        return memberEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
