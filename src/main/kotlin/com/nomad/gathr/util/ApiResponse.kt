package com.nomad.gathr.util

import com.nomad.gathr.common.dto.ApiResponse

object ApiResponseUtil {

    fun <T> success(data: T?, message: String = "요청이 성공했습니다."): ApiResponse<T> {
        return ApiResponse(
            status = "SUCCESS",
            code = 200,
            message = message,
            data = data
        )
    }

    fun fail(message: String, code: Int = 400): ApiResponse<Nothing> {
        return ApiResponse(
            status = "FAIL",
            code = code,
            message = message,
            data = null
        )
    }

    fun error(message: String, code: Int = 500): ApiResponse<Nothing> {
        return ApiResponse(
            status = "ERROR",
            code = code,
            message = message,
            data = null
        )
    }
}