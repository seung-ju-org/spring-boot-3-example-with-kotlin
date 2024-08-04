package com.example.redis.configs

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(private val redisProperties: RedisProperties) {
    @Bean
    fun redisStandaloneConfiguration(): RedisStandaloneConfiguration {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = redisProperties.host
        redisStandaloneConfiguration.port = redisProperties.port
        return redisStandaloneConfiguration
    }

    @Bean
    fun lettuceClientConfiguration(): LettuceClientConfiguration {
        val lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder()
        if (redisProperties.ssl.isEnabled) {
            lettuceClientConfigurationBuilder.useSsl()
        }
        return lettuceClientConfigurationBuilder.build()
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisStandaloneConfiguration(), lettuceClientConfiguration())
    }

    @Bean
    fun redisTemplate(objectMapper: ObjectMapper?): RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        val stringRedisSerializer = StringRedisSerializer()
        redisTemplate.keySerializer = stringRedisSerializer
        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer(objectMapper!!)
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        return redisTemplate
    }
}
