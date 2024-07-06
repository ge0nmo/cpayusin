package com.jbaacount.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jbaacount.global.oauth2.OAuth2Response;
import com.jbaacount.global.security.jwt.JwtService;
import com.jbaacount.model.Member;
import com.jbaacount.model.type.Platform;
import com.jbaacount.model.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {

    private final JwtService jwtService;
    private final MemberService memberService;
    private final FileService fileService;
    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();
    public final String BASIC_URL = "oauth2.";
    public final String GRANT_TYPE = "authorization_code";

    @Transactional
    public OAuth2Response oauth2Login(String code, String registrationId)
    {
        log.info("code = {}", code);
        String accessToken = getAccessToken(code, registrationId);
        log.info("authorization token = {}", accessToken);

        JsonNode userResource = getUserInfoFromToken(accessToken, registrationId);

        return userLoginProcess(userResource, registrationId);
    }

    private String getAccessToken(String code, String registrationId)
    {
        String clientId = env.getProperty(BASIC_URL + registrationId + ".client-id");
        String clientSecret = env.getProperty(BASIC_URL + registrationId + ".client-secret");
        String redirectUri = env.getProperty(BASIC_URL + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty(BASIC_URL + registrationId + ".token-uri");

        log.info("tokenuri = {}", tokenUri);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", GRANT_TYPE);
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> exchange = restTemplate.exchange(tokenUri, HttpMethod.POST, httpEntity, JsonNode.class);

        return exchange.getBody().get("access_token").asText();
    }

    private JsonNode getUserInfoFromToken(String accessToken, String registrationId)
    {
        String resourceUri = env.getProperty(BASIC_URL + registrationId + ".resource-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.info("entity = {}", entity);

        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

    private OAuth2Response userLoginProcess(JsonNode resource, String registrationId)
    {
        log.info("registrationId = {}", registrationId);

        switch (registrationId) {
            case "google":
                return processGoogleLogin(resource);

            case "kakao":
                return processKakaoLogin(resource);

            case "naver":
                return processNaverLogin(resource);

            default:
                throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
    }

    private OAuth2Response processNaverLogin(JsonNode resource)
    {
        JsonNode response = resource.get("response");

        String email = response.get("email").asText();
        String nickname = response.get("nickname").asText();
        String picture = response.get("profile_image").asText();
        Member member = saveOrUpdate(nickname, email, picture, Platform.NAVER.getValue());

        return createOAuth2Response(nickname, email, picture);
    }

    private OAuth2Response processKakaoLogin(JsonNode resource)
    {
        JsonNode properties = resource.get("properties");
        String nickname = properties.get("nickname").asText();
        String picture = properties.get("profile_image").asText();
        String email = resource.get("kakao_account").get("email").asText();

        Member member = saveOrUpdate(nickname, email, picture, Platform.KAKAO.getValue());

        return createOAuth2Response(nickname, email, picture);
    }

    private OAuth2Response processGoogleLogin(JsonNode resource)
    {
        String name = resource.get("name").asText();
        String email = resource.get("email").asText();
        String picture = resource.get("picture").asText();

        Member member = saveOrUpdate(name, email, picture, Platform.GOOGLE.getValue());

        return createOAuth2Response(name, email, picture);
    }

    private OAuth2Response createOAuth2Response(String name, String email, String picture)
    {
        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        return OAuth2Response.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role("USER")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Member saveOrUpdate(String name, String email, String picture, String registrationId)
    {
        Member member = memberService.findOptionalMemberByEmail(email)
                .orElseGet(() -> {
                    Member savedMember = memberService.save(Member.builder()
                            .nickname(name)
                            .email(email)
                            .password(UUID.randomUUID().toString())
                            .url(picture)
                            .role(Role.USER.getValue())
                            .platform(Platform.platformValue(registrationId))
                            .build());

                    Optional.ofNullable(picture)
                            .ifPresent(url -> fileService.saveForOauth2(url, savedMember));
                    return savedMember;
                });

        member.setNickname(name);
        Optional.ofNullable(picture)
                .ifPresent(url -> fileService.updateForOAuth2(url, member));

        return member;
    }
}
