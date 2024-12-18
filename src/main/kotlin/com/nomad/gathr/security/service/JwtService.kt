package com.nomad.gathr.security.service

import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.security.entity.TokenStatus
import com.nomad.gathr.security.util.JwtGenerator
import com.nomad.gathr.security.util.JwtUtil
import com.nomad.gathr.util.RedisUtil
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class JwtService(
    private val jwtUtil: JwtUtil,
    private val jwtGenerator: JwtGenerator,
    private val redisUtil: RedisUtil
) {

    /** Access Token 재발급 **/
    fun reissueAccessToken(refreshToken: String, user: User): String {
        if (jwtUtil.getTokenStatus(refreshToken) != TokenStatus.AUTHENTICATED) {
            throw IllegalArgumentException("유효하지 않은 재발급 토큰입니다.")
        }

        val storedRefreshToken = redisUtil.getValue("refresh:${user.id}")
        if (storedRefreshToken != refreshToken) {
            throw IllegalArgumentException("재발급 토큰이 일치하지 않습니다.")
        }

        // 새로운 Access Token 생성
        return jwtGenerator.createAccessToken(user)
    }

    /** 토큰 무효화 **/
    fun invalidateToken(accessToken: String, refreshToken: String, user: User) {
        val expirationMillis = jwtGenerator.getAccessTokenExpirationPeriod() - (System.currentTimeMillis() - getTokenIssueTime(accessToken))
        redisUtil.setValueWithExpiration(
            "blacklist:$accessToken",
            "blacklisted",
            Duration.ofMillis(expirationMillis)
        )

        redisUtil.delete("refresh:${user.id}")
    }

    /** Refresh Token 저장 **/
    fun storeRefreshToken(user: User, refreshToken: String) {
        redisUtil.setValueWithExpiration(
            "refresh:${user.id}",
            refreshToken,
            Duration.ofMillis(jwtGenerator.getRefreshTokenExpirationPeriod())
        )
    }

    /** Access Token의 발급 시간을 추출 (만료 시간과 함께 계산) **/
    private fun getTokenIssueTime(token: String): Long {
        val claims = Jwts.parser()
            .verifyWith(jwtGenerator.key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.issuedAt.time
    }

    /** Access Token 블랙리스트 검사 **/
    fun isAccessTokenBlacklisted(accessToken: String): Boolean {
        return redisUtil.hasKey("blacklist:$accessToken")
    }

    /** Refresh Token 검증 **/
    fun isRefreshTokenValid(username: String, refreshToken: String): Boolean {
        val storedToken = redisUtil.getValue("refresh:$username")
        return storedToken == refreshToken
    }
}