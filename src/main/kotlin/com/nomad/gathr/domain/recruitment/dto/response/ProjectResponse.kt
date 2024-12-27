package com.nomad.gathr.domain.recruitment.dto.response

import com.nomad.gathr.domain.recruitment.entity.*
import java.time.LocalDate
import java.time.LocalDateTime

data class ProjectResponse(
    val topic: ProjectTopic,
    val output: String,
    override val id: Long,
    override val creatorId: Long,
    override val creatorName: String,
    override val title: String,
    override val description: String?,
    override val startDate: LocalDate,
    override val endDate: LocalDate,
    val currentParticipants: Int,
    override val maxParticipants: Int,
    override val locationType: LocationType,
    override val location: String?,
    override val link: String?,
    override val status: RecruitmentStatus,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime?,
    override val recruitmentType: RecruitmentType
) : RecruitmentResponse(
    id = id,
    creatorId = creatorId,
    creatorName = creatorName,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    currentParticipant = currentParticipants,
    maxParticipants = maxParticipants,
    locationType = locationType,
    location = location,
    link = link,
    status = status,
    recruitmentType = recruitmentType,
    createdAt = createdAt,
    updatedAt = updatedAt
)