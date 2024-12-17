package com.nomad.gathr.domain.authentication.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "아이디를 입력해주세요.")
    @field:Pattern(
        regexp = "^[a-z0-9]{4,20}$",
        message = "아이디는 영문 소문자와 숫자만 사용하며 4자 이상 20자 이하로 입력해주세요."
    )
    val username: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,20}$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String,

    @field:NotBlank(message = "이메일을 입력해주세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val email: String,

    @field:NotBlank(message = "이름을 입력해주세요.")
    @field:Pattern(
        regexp = "^[a-zA-Z가-힣\\s]{2,50}$",
        message = "이름은 한글 또는 영문만 입력 가능하며 2자 이상 50자 이하로 입력해주세요."
    )
    val name: String
)
