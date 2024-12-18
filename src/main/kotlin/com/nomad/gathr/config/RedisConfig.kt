package com.nomad.gathr.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: Int
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = host
        redisStandaloneConfiguration.port = port

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(connectionfactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = connectionfactory
        return redisTemplate
    }
}