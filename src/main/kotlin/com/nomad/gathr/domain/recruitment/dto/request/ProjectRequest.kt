package com.nomad.gathr.domain.recruitment.dto.request

import com.nomad.gathr.domain.recruitment.entity.LocationType
import com.nomad.gathr.domain.recruitment.entity.ProjectTopic
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class ProjectRequest(
    @field:NotNull(message = "프로젝트 주제를 선택해주세요.")
    val topic: ProjectTopic,

    @field:NotBlank(message = "프로젝트의 목표를 입력해주세요.")
    val output: String,

    @field:NotBlank(message = "프로젝트 제목을 입력해주세요.")
    override val title: String,

    @field:Size(max = 2000, message = "프로젝트 소개는 최대 2000자까지 입력 가능합니다.")
    override val description: String?,

    @field:NotNull(message = "프로젝트 모집 시작일을 입력해주세요.")
    override val startDate: LocalDate,

    @field:NotNull(message = "프로젝트 모집 종료일을 입력해주세요.")
    override val endDate: LocalDate,

    @field:Positive(message = "최대 참가 인원은 1명 이상이어야 합니다.")
    override val maxParticipants: Int,

    @field:NotNull(message = "프로젝트 모임 방식을 선택해주세요.")
    override val locationType: LocationType,

    override val location: String?,
    override val link: String?,
) : RecruitmentRequest(
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    maxParticipants = maxParticipants,
    locationType = locationType,
    location = location,
    link = link
)