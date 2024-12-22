package com.nomad.gathr.domain.user.dto.request

data class GeneratePreSignedUrlRequest(
    val fileName: String,
    val fileType: String
)