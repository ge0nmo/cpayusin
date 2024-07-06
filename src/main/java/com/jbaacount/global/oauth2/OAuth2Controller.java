package com.jbaacount.global.oauth2;

import com.google.common.net.HttpHeaders;
import com.jbaacount.global.security.jwt.JwtService;
import com.jbaacount.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/login/oauth2")
@RestController
public class OAuth2Controller
{
    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;

    @Value("${frontend-uri}")
    private String REDIRECT_URI;

    @GetMapping("/code/{registrationId}")
    public void getGoogleUser(@RequestParam("code") String code,
                              @PathVariable("registrationId") String registrationId,
                              HttpServletResponse response) throws IOException
    {
        OAuth2Response data = oAuth2Service.oauth2Login(code, registrationId);

        Pair<ResponseCookie, ResponseCookie> pairCookies = constructCookie(data);
        response.setHeader(HttpHeaders.SET_COOKIE, pairCookies.getLeft().toString());
        response.setHeader(HttpHeaders.SET_COOKIE, pairCookies.getRight().toString());

        // Redirect to frontend
        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .toUriString();
        response.sendRedirect(redirectUrl);
    }


    private Pair<ResponseCookie, ResponseCookie> constructCookie(OAuth2Response response)
    {
        ResponseCookie accessTokenCookie = jwtService.generateCookie(response.getAccessToken());
        ResponseCookie refreshTokenCookie = jwtService.generateCookie(response.getRefreshToken());

        return Pair.of(accessTokenCookie, refreshTokenCookie);
    }
}
