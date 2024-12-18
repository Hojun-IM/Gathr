package com.nomad.gathr.domain.authentication.dto

import com.nomad.gathr.domain.user.entity.Role

data class SignInResponse(
    val username: String,
    val email: String,
    val role: Role
)
