package com.nomad.gathr.security.util

import com.nomad.gathr.security.entity.TokenStatus
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class JwtUtil(
    private val jwtGenerator: JwtGenerator
) {

    /** 토큰 상태를 확인 **/
    fun getTokenStatus(token: String): TokenStatus {
        return try {
            Jwts.parser()
                .verifyWith(jwtGenerator.key)
                .build()
                .parseSignedClaims(token)
            return TokenStatus.AUTHENTICATED
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException, is IllegalArgumentException -> TokenStatus.EXPIRED
                is JwtException -> TokenStatus.INVALID
                else -> TokenStatus.INVALID
            }
        }
    }

    /** Access Token 추출 **/
    fun resolveAccessToken(request: HttpServletRequest): Optional<String> {
        val token = request.getHeader("Authorization")
        return if (token != null && token.startsWith("Bearer ")) {
            Optional.of(token.substring(7))
        } else {
            Optional.empty()
        }
    }

    /** Refresh Token 추출 **/
    fun resolveRefreshTokenFromCookie(request: HttpServletRequest): Optional<String> {
        val cookieVal = findCookieByName(request, "Refresh")
        return Optional.ofNullable(cookieVal)
    }

    /** Access Token 쿠키 설정 **/
    fun addAccessTokenToResponse(response: HttpServletResponse, accessToken: String) {
        response.setHeader("Authorization", "Bearer $accessToken")
    }

    /** Refresh Token 쿠키 설정 **/
    fun addRefreshTokenToCookie(response: HttpServletResponse, refreshToken: String) {
        val refreshTokenCookie = Cookie("Refresh", refreshToken).apply {
            isHttpOnly = true
            // secure = true // HTTPS 환경에서 활성화
            path = "/"
            maxAge = (jwtGenerator.getRefreshTokenExpirationPeriod() / 1000).toInt()
        }
        response.addCookie(refreshTokenCookie)
    }

    /** Refresh Token 쿠키 삭제 **/
    fun expireRefreshTokenCookie(response: HttpServletResponse) {
        val refreshTokenCookie = Cookie("Refresh", null).apply {
            isHttpOnly = true
            // secure = true // HTTPS 환경에서 활성화
            maxAge = 0
            path = "/"
        }
        response.addCookie(refreshTokenCookie)
    }

    /** Access Token 검증 **/
    fun validateAccessToken(token: String): Boolean {
        return getTokenStatus(token) == TokenStatus.AUTHENTICATED
    }

    /** Refresh Token 검증 **/
    fun validateRefreshToken(token: String): Boolean {
        return getTokenStatus(token) == TokenStatus.AUTHENTICATED
    }

    /** 토큰에서 username 추출 **/
    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(jwtGenerator.key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }

    private fun findCookieByName(request: HttpServletRequest, name: String): String? {
        val cookies = request.cookies ?: return null
        return cookies.firstOrNull { it.name == name }?.value
    }
}