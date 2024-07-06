package com.jbaacount.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig
{
    private final String host;
    private final int port;
    private final String password;

    public RedisConfig(@Value("${spring.data.redis.host}") String host,
                       @Value("${spring.data.redis.port}") int port,
                       @Value("${spring.data.redis.password}") String password)
    {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    /**
     * RedisConnectionFactory = redis 에 접근하기 위한 redis 저장소와 연결할 때 필요
     * RedisStandaloneConfiguration = single node 에 redis 를 연결하기 위한 설정 정보를 가지고 있는 기본 클래스
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory()
    {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setHostName(host);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     *  redisTemplate 은 redis 서버에 redis 커맨드 수행을 하기 위한 것을 제공
     *  setKeySerializer(), setValueSerializer() 설정해주는 이유는
     *  RedisTemplate 를 사용할 때 Spring - Redis 간 데이터 직렬화, 역직렬화 시 사용하는 방식이 Jdk 직렬화 방식이기 때문
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate()
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}
