package com.nomad.gathr.security.util

import com.nomad.gathr.domain.user.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Configuration
class JwtGenerator(
    @Value("\${security.jwt.secret-key}")
    private var secretKey: String,
    @Value("\${security.jwt.expiration}")
    private val accessTokenExpirationPeriod: Long,
    @Value("\${security.jwt.refresh-expiration}")
    private val refreshTokenExpirationPeriod: Long
) {

    lateinit var key: SecretKey

    @PostConstruct
    fun initializeKey() {
        key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    /** Access Token 생성 **/
    fun createAccessToken(user: User): String {
        val now = Date()
        val claims = mapOf(
            "sub" to user.username,
            "id" to user.id,
            "email" to user.email,
            "name" to user.name,
            "role" to user.role.key
        )

        return Jwts.builder()
            .claims(claims)
            .expiration(Date(now.time + accessTokenExpirationPeriod))
            .signWith(key)
            .compact()
    }

    /** Refresh Token 생성 **/
    fun createRefreshToken(user: User): String {
        val now = Date()
        return Jwts.builder()
            .subject(user.username)
            .expiration(Date(now.time + refreshTokenExpirationPeriod))
            .signWith(key)
            .compact()
    }

    fun getAccessTokenExpirationPeriod() = accessTokenExpirationPeriod
    fun getRefreshTokenExpirationPeriod() = refreshTokenExpirationPeriod
}