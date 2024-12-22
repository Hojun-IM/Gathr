package com.nomad.gathr.domain.user.dto.response

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val bio: String?,
)