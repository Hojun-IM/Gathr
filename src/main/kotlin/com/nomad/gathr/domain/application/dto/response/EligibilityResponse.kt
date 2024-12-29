package com.nomad.gathr.domain.application.dto.response

data class EligibilityResponse(
    val isEligible: Boolean,
    val message: String
)