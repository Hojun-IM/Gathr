package com.nomad.gathr.security.filter

import com.nomad.gathr.config.SecurityConfig
import com.nomad.gathr.domain.user.dto.CustomUserDetails
import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.domain.user.service.CustomUserDetailsService
import com.nomad.gathr.execption.custom.InvalidAccessTokenException
import com.nomad.gathr.security.service.JwtService
import com.nomad.gathr.security.util.JwtUtil
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.Throws

class JwtAuthenticationFilter(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtService: JwtService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken = jwtUtil.resolveAccessToken(request).orElse(null)

            if (accessToken != null && jwtUtil.validateAccessToken(accessToken)) {
                if (jwtService.isAccessTokenBlacklisted(accessToken)) {
                    throw InvalidAccessTokenException()
                }

                val username = jwtUtil.getUsernameFromToken(accessToken)
                val userDetails = customUserDetailsService.loadUserByUsername(username)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                // 재발급 토큰을 이용한 인증 토큰 재발급
                val refreshToken = jwtUtil.resolveRefreshTokenFromCookie(request).orElse(null)
                if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
                    val username = jwtUtil.getUsernameFromToken(refreshToken)
                    val userDetails = customUserDetailsService.loadUserByUsername(username)

                    if (userDetails is CustomUserDetails) {
                        val user = userDetails.getUser()

                        // Refresh Token 검증
                        if (jwtService.isRefreshTokenValid(username, refreshToken)) {
                            val newAccessToken = jwtService.reissueAccessToken(refreshToken, user)
                            jwtUtil.addAccessTokenToResponse(response, newAccessToken)

                            val newAuthentication = UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.authorities
                            )
                            SecurityContextHolder.getContext().authentication = newAuthentication
                        }
                    }
                }
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return SecurityConfig.PERMITTED_URLS.any { request.requestURI.startsWith(it) }
    }
}