package com.nomad.gathr.security.dto

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String
)
