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
import com.nomad.gathr.execption.custom.EmailAlreadyExistsException
import com.nomad.gathr.execption.custom.UsernameAlreadyExistsException
import com.nomad.gathr.security.dto.TokenInfo
import com.nomad.gathr.security.service.JwtService
import com.nomad.gathr.security.util.JwtGenerator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.security.sasl.AuthenticationException

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator,
    private val jwtService: JwtService,
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
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(signInRequest.username, signInRequest.password)
            )
            val user = userRepository.findByUsername(signInRequest.username)
                .orElseThrow { UsernameNotFoundException("존재하지 않는 아이디: ${signInRequest.username}") }

            val accessToken = jwtGenerator.createAccessToken(user)
            val refreshToken = jwtGenerator.createRefreshToken(user)

            jwtService.storeRefreshToken(user, refreshToken)

            val signInResponse = SignInResponse(
                username = user.username,
                email = user.email,
                role = user.role
            )

            val tokenInfo = TokenInfo(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            return SignInResult(
                signInResponse = signInResponse,
                tokenInfo = tokenInfo
            )
        } catch (e: org.springframework.security.core.AuthenticationException) {
            throw AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다.")
        }
    }

    @Transactional
    fun signOut(user: User, accessToken: String, refreshToken: String) {
        jwtService.invalidateToken(accessToken, refreshToken, user)
    }

    fun loadUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다: $username") }
    }

    private fun validateSignUpRequest(signUpRequest: SignUpRequest) {
        if (userRepository.findByUsername(signUpRequest.username).isPresent) {
            throw UsernameAlreadyExistsException()
        }
        if (userRepository.findByEmail(signUpRequest.email).isPresent) {
            throw EmailAlreadyExistsException()
        }
    }
}