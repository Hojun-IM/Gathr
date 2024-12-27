package com.nomad.gathr.domain.recruitment.mapper

import com.nomad.gathr.domain.recruitment.dto.request.ProjectRequest
import com.nomad.gathr.domain.recruitment.dto.response.ProjectResponse
import com.nomad.gathr.domain.recruitment.entity.Project
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.entity.RecruitmentType
import com.nomad.gathr.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class ProjectRecruitmentMapper {
    fun toEntity(request: ProjectRequest, creator: User): Project {
        return Project(
            creator = creator,
            title = request.title,
            description = request.description,
            startDate = request.startDate,
            endDate = request.endDate,
            maxParticipants = request.maxParticipants,
            locationType = request.locationType,
            location = request.location,
            link = request.link,
            status = RecruitmentStatus.OPEN,
            topic = request.topic,
            output = request.output
        )
    }

    fun toResponse(project: Project): ProjectResponse {
        return ProjectResponse(
            id = project.id!!,
            topic = project.topic,
            output = project.output,
            creatorId = project.creator.id!!,
            creatorName = project.creator.name,
            title = project.title,
            description = project.description,
            startDate = project.startDate,
            endDate = project.endDate,
            currentParticipants = project.currentParticipants,
            maxParticipants = project.maxParticipants,
            locationType = project.locationType,
            location = project.location,
            link = project.link,
            status = project.status,
            createdAt = project.createdAt!!,
            updatedAt = project.updatedAt,
            recruitmentType = RecruitmentType.PROJECT
        )
    }
}