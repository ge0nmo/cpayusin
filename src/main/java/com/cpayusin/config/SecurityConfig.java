package com.cpayusin.config;

import com.cpayusin.common.handler.CustomAccessDeniedHandler;
import com.cpayusin.common.handler.CustomAuthenticationEntryPoint;
import com.cpayusin.common.handler.CustomAuthenticationFailureHandler;
import com.cpayusin.common.handler.CustomAuthenticationSuccessfulHandler;
import com.cpayusin.common.security.filter.JwtAuthenticationFilter;
import com.cpayusin.common.security.filter.JwtVerificationFilter;
import com.cpayusin.common.security.jwt.JwtService;
import com.cpayusin.common.security.userdetails.MemberDetailsService;
import com.cpayusin.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final JwtService jwtService;
    private final RedisService redisService;
    private final MemberDetailsService memberDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                .headers((headers) ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/login",
                                "/api/v1/sign-up",
                                "/api/v1/mail/**",
                                "/api/v1/verification",
                                "/api/v1/member/verify-email",
                                "/api/v1/member/verify-nickname",
                                "/api/v1/file"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST).authenticated()
                        .requestMatchers(HttpMethod.PATCH).authenticated()
                        .requestMatchers(HttpMethod.DELETE).authenticated()
                        .anyRequest().permitAll())
                .apply(new CustomFilterConfigurer());


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://www.cpayusin.com.s3-website.ap-northeast-2.amazonaws.com",
                "https://cpayusin.com",
                "https://www.cpayusin.com",
                "https://dxpqbs6f37n1l.cloudfront.net",
                "http://13.124.241.118:8080"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity>
    {
        @Override
        public void configure(HttpSecurity builder) throws Exception
        {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter =
                    new JwtAuthenticationFilter(authenticationManager, jwtService, redisService, memberDetailsService);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessfulHandler(memberDetailsService));
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtService, memberDetailsService);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
