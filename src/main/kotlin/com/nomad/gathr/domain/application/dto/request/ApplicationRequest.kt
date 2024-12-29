package com.nomad.gathr.domain.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ApplicationRequest(
    @field:NotNull(message = "지원할 모집의 ID를 입력해주세요.")
    val recruitmentId: Long,

    @field:NotBlank(message = "지원서를 작성해주세요.")
    val applicationMessage: String
)
