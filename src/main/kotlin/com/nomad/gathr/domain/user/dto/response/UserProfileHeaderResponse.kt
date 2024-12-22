package com.nomad.gathr.domain.user.dto.response

data class UserProfileHeaderResponse(
    val userId: Long,
    val username: String,
    val name: String,
    val profileImageUrl: String?,
)
