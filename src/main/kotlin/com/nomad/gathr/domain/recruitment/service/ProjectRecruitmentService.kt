package com.nomad.gathr.domain.recruitment.service

import com.nomad.gathr.domain.recruitment.dto.request.ProjectRequest
import com.nomad.gathr.domain.recruitment.dto.response.ProjectResponse
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.mapper.ProjectRecruitmentMapper
import com.nomad.gathr.domain.recruitment.repository.ProjectRepository
import com.nomad.gathr.domain.recruitment.validator.RecruitmentValidator
import com.nomad.gathr.domain.user.repository.UserRepository
import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class ProjectRecruitmentService(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val recruitmentValidator: RecruitmentValidator,
    private val projectRecruitmentMapper: ProjectRecruitmentMapper
) : RecruitmentService<ProjectRequest, ProjectResponse> {

    override fun getRecruitment(id: Long): ProjectResponse {
        val project = projectRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }
        return projectRecruitmentMapper.toResponse(project)
    }

    override fun getRecruitments(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<ProjectResponse> {
        val studies = projectRepository.findByFilters(
            title = title,
            status = status,
            startDate = startDate,
            endDate = endDate,
            locationType = locationType,
            pageable = pageable
        )
        return studies.map { projectRecruitmentMapper.toResponse(it) }
    }

    @Transactional
    override fun createRecruitment(userId: Long, request: ProjectRequest): ProjectResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        recruitmentValidator.validateDate(request.startDate, request.endDate)

        val project = projectRecruitmentMapper.toEntity(request, user)
        project.currentParticipants = 1
        val savedproject = projectRepository.save(project)

        // TODO: 사용자 Participant 추가하는 로직 구현

        return projectRecruitmentMapper.toResponse(savedproject)
    }

    @Transactional
    override fun updateRecruitment(userId: Long, id: Long, request: ProjectRequest): ProjectResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val project = projectRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (project.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        recruitmentValidator.validateDate(request.startDate, request.endDate)

        project.apply {
            title = request.title
            description = request.description
            startDate = request.startDate
            endDate = request.endDate
            maxParticipants = request.maxParticipants
            locationType = request.locationType
            location = request.location
            link = request.link
            topic = request.topic
            output = request.output
        }

        return projectRecruitmentMapper.toResponse(project)
    }

    @Transactional
    override fun deleteRecruitment(userId: Long, id: Long): ProjectResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val project = projectRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (project.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        projectRepository.delete(project)
        return projectRecruitmentMapper.toResponse(project)
    }


    @Transactional
    override fun closeRecruitment(userId: Long, id: Long): ProjectResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val project = projectRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (project.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        project.status = RecruitmentStatus.CLOSED
        return projectRecruitmentMapper.toResponse(project)
    }

    @Transactional
    override fun openRecruitment(userId: Long, id: Long): ProjectResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val project = projectRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (project.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        recruitmentValidator.validateOpenDate(project.startDate, project.endDate)

        project.status = RecruitmentStatus.OPEN
        return projectRecruitmentMapper.toResponse(project)
    }
}