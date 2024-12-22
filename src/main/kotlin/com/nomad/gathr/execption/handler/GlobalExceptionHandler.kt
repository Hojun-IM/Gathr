package com.nomad.gathr.execption.handler

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.util.ApiResponseUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ApiResponse<Nothing> {
        logger.error {"[Exception] 예외: ${ex.errorCode}, 예외 코드: ${ex.errorCode.code}, 메시지: ${ex.message}" }
        return ApiResponseUtil.customFail(ex.errorCode.message, ex.errorCode.code)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ApiResponse<Nothing> {
        logger.error {"[Exception] 예외: ${ex.message}, 예외 코드: 401" }
        return ApiResponseUtil.fail("아이디 또는 비밀번호가 일치하지 않습니다.", 401)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ApiResponse<Nothing> {
        logger.error {"[Exception] 예외: ${ex.message}, 예외 코드: 505" }
        return ApiResponseUtil.error("서버 오류가 발생했습니다.", 505)
    }
}