package com.nomad.gathr.domain.recruitment.dto.response

import com.nomad.gathr.domain.recruitment.entity.LocationType
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.entity.RecruitmentType
import java.time.LocalDate
import java.time.LocalDateTime

open class RecruitmentResponse(
    open val id: Long,
    open val creatorId: Long,
    open val creatorName: String,
    open val title: String,
    open val description: String?,
    open val startDate: LocalDate,
    open val endDate: LocalDate,
    open val currentParticipant: Int,
    open val maxParticipants: Int,
    open val locationType: LocationType,
    open val location: String?,
    open val link: String?,
    open val status: RecruitmentStatus,
    open val recruitmentType: RecruitmentType,
    open val createdAt: LocalDateTime,
    open val updatedAt: LocalDateTime?
)