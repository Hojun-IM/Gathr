package com.nomad.gathr.domain.authentication.dto

import com.nomad.gathr.security.dto.TokenInfo

data class SignInResult(
    val signInResponse: SignInResponse,
    val tokenInfo: TokenInfo
)
