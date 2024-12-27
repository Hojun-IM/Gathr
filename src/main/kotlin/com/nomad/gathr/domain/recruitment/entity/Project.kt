package com.nomad.gathr.domain.recruitment.entity

import com.nomad.gathr.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "project")
@DiscriminatorValue("PROJECT")
class Project(
    creator: User,
    title: String,
    description: String? = null,
    startDate: LocalDate,
    endDate: LocalDate,
    maxParticipants: Int,
    locationType: LocationType,
    location: String? = null,
    link: String? = null,
    status: RecruitmentStatus = RecruitmentStatus.OPEN,

    @Column(nullable = false)
    var topic: ProjectTopic,

    @Column(nullable = false)
    var output: String
) : Recruitment(
    creator = creator,
    title = title,
    description = description,
    recruitmentType = RecruitmentType.PROJECT,
    requirement = Requirement.NONE,
    startDate = startDate,
    endDate = endDate,
    maxParticipants = maxParticipants,
    locationType = locationType,
    location = location,
    link = link,
    status = status
)
