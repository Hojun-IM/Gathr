package com.nomad.gathr.domain.application.service

import com.nomad.gathr.domain.application.dto.request.ApplicationRequest
import com.nomad.gathr.domain.application.dto.response.ApplicationResponse
import com.nomad.gathr.domain.application.dto.response.EligibilityResponse
import com.nomad.gathr.domain.application.entity.ApplicationStatus
import com.nomad.gathr.domain.application.mapper.ApplicationMapper
import com.nomad.gathr.domain.application.repository.ApplicationRepository
import com.nomad.gathr.domain.application.validator.ApplicationValidator
import com.nomad.gathr.domain.recruitment.entity.Recruitment
import com.nomad.gathr.domain.recruitment.repository.ProjectRepository
import com.nomad.gathr.domain.recruitment.repository.StudyRepository
import com.nomad.gathr.domain.user.repository.UserRepository
import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val studyRepository: StudyRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val applicationMapper: ApplicationMapper,
    private val applicationValidator: ApplicationValidator
) {

    fun getApplicationsByUserId(userId: Long): List<ApplicationResponse> {
        val applications = applicationRepository.findByUserId(userId)
        return applications.map { applicationMapper.toResponse(it) }
    }

    fun getApplication(applicationId: Long, userId: Long): ApplicationResponse {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { CustomException(ErrorCode.APPLICATION_NOT_FOUND) }
        validateAccess(userId, application.user.id!!)
        return applicationMapper.toResponse(application)
    }

    @Transactional
    fun createApplication(
        userId: Long,
        request: ApplicationRequest
    ) : ApplicationResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
        val recruitment = findRecruitmentById(request.recruitmentId)
            ?: throw CustomException(ErrorCode.RECRUITMENT_NOT_FOUND)

        applicationValidator.validateEligibility(user, recruitment)

        val application = applicationMapper.toEntity(request, user, recruitment)
        val savedApplication = applicationRepository.save(application)

        return applicationMapper.toResponse(savedApplication)
    }

    @Transactional
    fun updateApplication(
        applicationId: Long,
        userId: Long,
        request: ApplicationRequest
    ): ApplicationResponse {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { CustomException(ErrorCode.APPLICATION_NOT_FOUND) }
        validateAccess(userId, application.user.id!!)

        application.applicationMessage = request.applicationMessage
        applicationRepository.save(application)

        return applicationMapper.toResponse(application)
    }

    @Transactional
    fun deleteApplication(applicationId: Long, userId: Long) {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { CustomException(ErrorCode.APPLICATION_NOT_FOUND) }
        validateAccess(userId, application.user.id!!)
        applicationRepository.delete(application)
    }

    @Transactional
    fun changeApplicationStatus(
        applicationId: Long,
        recruiterId: Long,
        status: String
    ): ApplicationResponse {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { CustomException(ErrorCode.APPLICATION_NOT_FOUND) }
        val recruitment = application.recruitment

        if (recruitment.creator.id != recruiterId) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        if (application.status != ApplicationStatus.PENDING) {
            throw CustomException(ErrorCode.INVALID_STATUS_CHANGE)
        }

        application.status = ApplicationStatus.valueOf(status)
        applicationRepository.save(application)

        return applicationMapper.toResponse(application)
    }

    fun getApplicationsByRecruitmentId(
        recruitmentId: Long,
        recruiterId: Long
    ): List<ApplicationResponse> {
        val recruitment = findRecruitmentById(recruitmentId)
            ?: throw CustomException(ErrorCode.RECRUITMENT_NOT_FOUND)

        if (recruitment.creator.id != recruiterId) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }

        val applications = applicationRepository.findByRecruitmentId(recruitmentId)
        return applications.map { applicationMapper.toResponse(it) }
    }

    fun checkApplicationEligibility(userId: Long, recruitmentId: Long): EligibilityResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
        val recruitment = findRecruitmentById(recruitmentId)
            ?: throw CustomException(ErrorCode.RECRUITMENT_NOT_FOUND)

        val (isEligible, message) = applicationValidator.checkEligibility(user, recruitment)
        return EligibilityResponse(isEligible, message!!)
    }

    private fun findRecruitmentById(recruitmentId: Long): Recruitment? {
        return studyRepository.findById(recruitmentId).orElse(null)
            ?: projectRepository.findById(recruitmentId).orElse(null)
    }

    fun validateAccess(currentUserId: Long, resourceOwnerId: Long) {
        if (currentUserId != resourceOwnerId) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }
    }
}