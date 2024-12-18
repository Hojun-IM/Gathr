package com.nomad.gathr.common.dto

data class ApiResponse<T>(
    val status: String,
    val code: Int,
    val message: String,
    val data: T? = null
)
