package com.nomad.gathr.domain.authentication.service

import com.nomad.gathr.domain.authentication.dto.SignInRequest
import com.nomad.gathr.domain.authentication.dto.SignInResponse
import com.nomad.gathr.domain.authentication.dto.SignInResult
import com.nomad.gathr.domain.authentication.dto.SignUpRequest
import com.nomad.gathr.domain.user.entity.AccountStatus
import com.nomad.gathr.domain.user.entity.AuthProvider
import com.nomad.gathr.domain.user.entity.Role
import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.domain.user.repository.UserRepository
import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import com.nomad.gathr.security.dto.TokenInfo
import com.nomad.gathr.security.service.JwtService
import com.nomad.gathr.security.util.JwtGenerator
import com.nomad.gathr.security.util.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator,
    private val jwtService: JwtService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun signUp(signUpRequest: SignUpRequest) {
        validateSignUpRequest(signUpRequest)
        val user = User(
            username = signUpRequest.username,
            email = signUpRequest.email,
            password = passwordEncoder.encode(signUpRequest.password),
            name = signUpRequest.name,
            provider = AuthProvider.LOCAL,
            accountStatus = AccountStatus.ACTIVE,
            role = Role.USER,
        )
        userRepository.save(user)
    }

    @Transactional
    fun signIn(signInRequest: SignInRequest): SignInResult {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(signInRequest.username, signInRequest.password)
        )
        val user = userRepository.findByUsername(signInRequest.username)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val accessToken = jwtGenerator.createAccessToken(user)
        val refreshToken = jwtGenerator.createRefreshToken(user)

        jwtService.storeRefreshToken(user, refreshToken)

        return SignInResult(
            signInResponse = SignInResponse(
                username = user.username,
                email = user.email,
                role = user.role
            ),
            tokenInfo = TokenInfo(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    @Transactional
    fun signOut(user: User, accessToken: String, refreshToken: String) {
        jwtService.invalidateToken(accessToken, refreshToken, user)
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): String {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw CustomException(ErrorCode.REFRESH_TOKEN_INVALID)
        }

        val username = jwtUtil.getUsernameFromToken(refreshToken)
        val user = userRepository.findByUsername(username)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        if (!jwtService.isRefreshTokenValid(user.id.toString(), refreshToken)) {
            throw CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCHED)
        }

        return jwtGenerator.createAccessToken(user)
    }

    fun loadUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
    }

    private fun validateSignUpRequest(signUpRequest: SignUpRequest) {
        if (userRepository.findByUsername(signUpRequest.username).isPresent) {
            throw CustomException(ErrorCode.USERNAME_DUPLICATION)
        }
        if (userRepository.findByEmail(signUpRequest.email).isPresent) {
            throw CustomException(ErrorCode.EMAIL_DUPLICATION)
        }
    }
}