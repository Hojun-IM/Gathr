package com.nomad.gathr.domain.application.dto.response

import com.nomad.gathr.domain.application.entity.ApplicationStatus

data class ApplicationResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val recruitmentId: Long,
    val recruitmentTitle: String,
    val status: ApplicationStatus,
    val applicationMessage: String
)
