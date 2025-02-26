package org.example.libdev.global.config;

import org.example.libdev.availability.dto.AvailabilityDTO;
import org.example.libdev.availability.entity.Availability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("호스트는 'null' 이거나 비어 있을 수 없습니다.");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("포트는 0 보다 커야 합니다.");
        }

        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, AvailabilityDTO> redisTemplate() {
        RedisTemplate<String, AvailabilityDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        Jackson2JsonRedisSerializer<AvailabilityDTO> serializer = new Jackson2JsonRedisSerializer<>(AvailabilityDTO.class);

        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
