package com.cpayusin.common.security.filter;

import com.cpayusin.common.security.jwt.JwtService;
import com.cpayusin.common.security.userdetails.MemberDetails;
import com.cpayusin.common.security.userdetails.MemberDetailsService;
import com.cpayusin.common.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final MemberDetailsService memberDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        try {
            String accessToken = resolveAccessToken(request);
            jwtService.isValidToken(accessToken);
            setAuthenticationToContext(jwtService.getClaims(accessToken));

        } catch (InvalidTokenException e){
            log.error("Error processing JWT: {}", e.getMessage());
            throw e;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        String authorization = request.getHeader(AUTHORIZATION);

        return authorization == null || !authorization.startsWith("Bearer ");
    }

    private void setAuthenticationToContext(Claims claims)
    {
        String email = claims.getSubject();
        MemberDetails memberDetails = memberDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveAccessToken(HttpServletRequest request)
    {
        return request.getHeader(AUTHORIZATION).substring(7);
    }
}