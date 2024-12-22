package com.nomad.gathr.domain.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserProfileRequest(
    @field:NotBlank(message = "변경할 이름을 입력해주세요.")
    @field:Pattern(
        regexp = "^[a-zA-Z가-힣\\s]{2,50}$",
        message = "이름은 한글 또는 영문만 입력 가능하며 2자 이상 50자 이하로 입력해주세요."
    )
    val name: String,

    @field:Size(max = 500, message = "자기소개는 500자 이하로 입력해주세요.")
    val bio: String?
)
