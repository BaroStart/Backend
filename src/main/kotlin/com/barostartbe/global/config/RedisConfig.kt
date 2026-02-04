package com.barostartbe.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.json.JsonMapper

@Configuration
class RedisConfig(
    val objectMapper: JsonMapper
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory()

    @Bean
    fun redisTemplate() : RedisTemplate<String, Any>{
        return RedisTemplate<String, Any>().apply {
            connectionFactory = redisConnectionFactory()
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJacksonJsonRedisSerializer(objectMapper)
        }
    }
}