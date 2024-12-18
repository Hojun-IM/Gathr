package com.nomad.gathr.execption

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.execption.custom.EmailAlreadyExistsException
import com.nomad.gathr.execption.custom.UsernameAlreadyExistsException
import com.nomad.gathr.util.ApiResponseUtil
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ApiResponse<Nothing> {
        return ApiResponseUtil.fail("인증 오류가 발생했습니다.", 401)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException): ApiResponse<Nothing> {
        return ApiResponseUtil.fail("사용자를 찾을 수 없습니다.", 404)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ApiResponse<Nothing> {
        return ApiResponseUtil.fail("리소스를 찾을 수 없습니다.", 404)
    }

    @ExceptionHandler(UsernameAlreadyExistsException::class)
    fun handleUsernameAlreadyExistsException(ex: UsernameAlreadyExistsException): ApiResponse<Nothing> {
        return ApiResponseUtil.fail(ex.message ?: "이미 존재하는 사용자 이름입니다.", 409)
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(ex: EmailAlreadyExistsException): ApiResponse<Nothing> {
        return ApiResponseUtil.fail(ex.message ?: "이미 존재하는 이메일입니다.", 409)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ApiResponse<Nothing> {
        return ApiResponseUtil.error("서버 오류가 발생했습니다.", 505)
    }
}