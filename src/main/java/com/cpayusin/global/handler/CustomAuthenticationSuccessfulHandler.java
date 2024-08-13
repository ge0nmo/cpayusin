package com.cpayusin.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cpayusin.global.security.userdetails.MemberDetailsService;
import com.cpayusin.model.Member;
import com.cpayusin.payload.response.AuthenticationResponse;
import com.cpayusin.payload.response.GlobalResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessfulHandler implements AuthenticationSuccessHandler
{
    private final MemberDetailsService memberDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        String email = authentication.getName();
        Member member = memberDetailsService.loadUserByUsername(email).getMember();
        AuthenticationResponse data = AuthenticationResponse.builder()
                .email(email)
                .role(member.getRole())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(new GlobalResponse<>(data));

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonResponse);
        printWriter.flush();
    }
}
