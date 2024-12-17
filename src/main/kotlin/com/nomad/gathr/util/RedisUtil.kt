package com.nomad.gathr.util

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisUtil(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun setValue(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun setValueWithExpiration(key: String, value: Any, duration: Duration) {
        redisTemplate.opsForValue().set(key, value, duration)
    }

    fun getValue(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    fun delete(key: String) {
        redisTemplate.delete(key)
    }

    fun hasKey(key: String): Boolean {
        return redisTemplate.hasKey(key) ?: false
    }
}