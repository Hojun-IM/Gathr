package com.nomad.gathr.domain.recruitment.mapper

import com.nomad.gathr.domain.recruitment.dto.request.StudyRequest
import com.nomad.gathr.domain.recruitment.dto.response.StudyResponse
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.entity.RecruitmentType
import com.nomad.gathr.domain.recruitment.entity.Study
import com.nomad.gathr.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class StudyRecruitmentMapper {
    fun toEntity(request: StudyRequest, creator: User): Study {
        return Study(
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
            goal = request.goal
        )
    }

    fun toResponse(study: Study): StudyResponse {
        return StudyResponse(
            id = study.id!!,
            topic = study.topic,
            goal = study.goal,
            creatorId = study.creator.id!!,
            creatorName = study.creator.name,
            title = study.title,
            description = study.description,
            startDate = study.startDate,
            endDate = study.endDate,
            currentParticipants = study.currentParticipants,
            maxParticipants = study.maxParticipants,
            locationType = study.locationType,
            location = study.location,
            link = study.link,
            status = study.status,
            createdAt = study.createdAt!!,
            updatedAt = study.updatedAt,
            recruitmentType = RecruitmentType.STUDY
        )
    }
}