package com.nomad.gathr.domain.recruitment.service

import com.nomad.gathr.domain.recruitment.dto.request.StudyRequest
import com.nomad.gathr.domain.recruitment.dto.response.StudyResponse
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.mapper.StudyRecruitmentMapper
import com.nomad.gathr.domain.recruitment.repository.StudyRepository
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
class StudyRecruitmentService(
    private val userRepository: UserRepository,
    private val studyRepository: StudyRepository,
    private val recruitmentValidator: RecruitmentValidator,
    private val studyRecruitmentMapper: StudyRecruitmentMapper
) : RecruitmentService<StudyRequest, StudyResponse> {

    override fun getRecruitment(id: Long): StudyResponse {
        val study = studyRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }
        return studyRecruitmentMapper.toResponse(study)
    }

    override fun getRecruitments(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<StudyResponse> {
        val studies = studyRepository.findByFilters(
            title = title,
            status = status,
            startDate = startDate,
            endDate = endDate,
            locationType = locationType,
            pageable = pageable
        )
        return studies.map { studyRecruitmentMapper.toResponse(it) }
    }

    @Transactional
    override fun createRecruitment(userId: Long, request: StudyRequest): StudyResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        recruitmentValidator.validateDate(request.startDate, request.endDate)

        val study = studyRecruitmentMapper.toEntity(request, user)
        study.currentParticipants = 1
        val savedStudy = studyRepository.save(study)

        // TODO: 사용자 Participant 추가하는 로직 구현

        return studyRecruitmentMapper.toResponse(savedStudy)
    }

    @Transactional
    override fun updateRecruitment(userId: Long, id: Long, request: StudyRequest): StudyResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val study = studyRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (study.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        recruitmentValidator.validateDate(request.startDate, request.endDate)

        study.apply {
            title = request.title
            description = request.description
            startDate = request.startDate
            endDate = request.endDate
            maxParticipants = request.maxParticipants
            locationType = request.locationType
            location = request.location
            link = request.link
            topic = request.topic
            goal = request.goal
        }

        return studyRecruitmentMapper.toResponse(study)
    }

    @Transactional
    override fun deleteRecruitment(userId: Long, id: Long): StudyResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val study = studyRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (study.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        studyRepository.delete(study)
        return studyRecruitmentMapper.toResponse(study)
    }


    @Transactional
    override fun closeRecruitment(userId: Long, id: Long): StudyResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val study = studyRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (study.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        study.status = RecruitmentStatus.CLOSED
        return studyRecruitmentMapper.toResponse(study)
    }

    @Transactional
    override fun openRecruitment(userId: Long, id: Long): StudyResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val study = studyRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.RECRUITMENT_NOT_FOUND) }

        if (study.creator.id != user.id) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        recruitmentValidator.validateOpenDate(study.startDate, study.endDate)

        study.status = RecruitmentStatus.OPEN
        return studyRecruitmentMapper.toResponse(study)
    }
}