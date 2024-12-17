package com.nomad.gathr.domain.authentication.controller

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.domain.authentication.dto.SignInRequest
import com.nomad.gathr.domain.authentication.dto.SignInResponse
import com.nomad.gathr.domain.authentication.dto.SignInResult
import com.nomad.gathr.domain.authentication.dto.SignUpRequest
import com.nomad.gathr.domain.authentication.service.AuthService
import com.nomad.gathr.security.service.JwtService
import com.nomad.gathr.security.util.JwtUtil
import com.nomad.gathr.util.ApiResponseUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil,
    private val jwtService: JwtService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid signUpRequest: SignUpRequest): ApiResponse<Nothing> {
        authService.signUp(signUpRequest)
        return ApiResponseUtil.success(
            data = null,
            message = "회원가입이 완료되었습니다."
        )
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid signInRequest: SignInRequest, response: HttpServletResponse): ApiResponse<SignInResponse> {
        val signInResult: SignInResult = authService.signIn(signInRequest)
        jwtUtil.addAccessTokenToResponse(response, signInResult.tokenInfo.accessToken)
        jwtUtil.addRefreshTokenToCookie(response, signInResult.tokenInfo.refreshToken)

        return ApiResponseUtil.success(
            data = signInResult.signInResponse,
            message = "로그인이 완료되었습니다."
        )
    }

    @PostMapping("/signout")
    fun signOut(request: HttpServletRequest, response: HttpServletResponse): ApiResponse<Nothing> {
        val accessToken = jwtUtil.resolveAccessToken(request).orElse(null)
        val refreshToken = jwtUtil.resolveRefreshTokenFromCookie(request).orElse(null)

        if (accessToken != null && refreshToken != null) {
            val username = jwtUtil.getUsernameFromToken(accessToken)
            val user = authService.loadUserByUsername(username)
            authService.signOut(user, accessToken, refreshToken)

            // Refresh Token 쿠키 삭제
            jwtUtil.expireRefreshTokenCookie(response)
        }

        SecurityContextHolder.clearContext()
        return ApiResponseUtil.success(
            data = null,
            message = "로그아웃이 완료되었습니다."
        )
    }

    @PostMapping("/token/refresh")
    fun refreshAccessToken(request: HttpServletRequest, response: HttpServletResponse): ApiResponse<Nothing> {
        val refreshToken = jwtUtil.resolveRefreshTokenFromCookie(request).orElse(null)
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            val username = jwtUtil.getUsernameFromToken(refreshToken)
            val user = authService.loadUserByUsername(username)

            // Refresh Token 검증
            if (jwtService.isRefreshTokenValid(username, refreshToken)) {
                val newAccessToken = jwtService.reissueAccessToken(refreshToken, user)
                jwtUtil.addAccessTokenToResponse(response, newAccessToken)

                // 새로운 Refresh Token을 발급하려면 아래 주석을 해제하고 새로운 Refresh Token을 생성 및 저장해야 합니다.
                // val newRefreshToken = jwtGenerator.createRefreshToken(user)
                // jwtService.storeRefreshToken(user, newRefreshToken)
                // jwtUtil.addRefreshTokenToCookie(response, newRefreshToken)

                return ApiResponseUtil.success(
                    data = null,
                    message = "인증 토큰이 갱신되었습니다."
                )
            }
        }
        return ApiResponseUtil.fail("재발급 토큰이 유효하지 않습니다.", 401)
    }
}