package com.nomad.gathr.domain.user.dto.response

data class GeneratePreSignedUrlResponse(
    val uploadUrl: String,
    val filePath: String
)
