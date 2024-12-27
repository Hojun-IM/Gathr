package com.nomad.gathr.domain.recruitment.dto.request

import com.nomad.gathr.domain.recruitment.entity.LocationType
import java.time.LocalDate
import java.time.LocalDateTime

open class RecruitmentRequest(
    open val title: String,
    open val description: String?,
    open val startDate: LocalDate,
    open val endDate: LocalDate,
    open val maxParticipants: Int,
    open val locationType: LocationType,
    open val location: String?,
    open val link: String?
)