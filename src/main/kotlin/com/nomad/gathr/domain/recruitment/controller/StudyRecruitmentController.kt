package com.nomad.gathr.domain.recruitment.controller

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.domain.recruitment.dto.request.StudyRequest
import com.nomad.gathr.domain.recruitment.dto.response.StudyResponse
import com.nomad.gathr.domain.recruitment.service.StudyRecruitmentService
import com.nomad.gathr.domain.user.dto.CustomUserDetails
import com.nomad.gathr.util.ApiResponseUtil
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/recruitment/study")
class StudyRecruitmentController(
    private val studyRecruitmentService: StudyRecruitmentService
) {

    @GetMapping("/{id}")
    fun getStudyRecruitment(
        @PathVariable id: Long
    ): ApiResponse<StudyResponse> {
        val response = studyRecruitmentService.getRecruitment(id)
        return ApiResponseUtil.success(response)
    }

    @GetMapping("/list")
    fun getStudyRecruitments(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(required = false) locationType: String?,
        pageable: Pageable
    ): ApiResponse<Page<StudyResponse>> {
        val response = studyRecruitmentService.getRecruitments(
            title = title,
            status = status,
            startDate = startDate,
            endDate = endDate,
            locationType = locationType,
            pageable = pageable
        )
        return ApiResponseUtil.success(response)
    }

    @PostMapping
    fun createStudyRecruitment(
        @RequestBody @Valid request: StudyRequest,
        authentication: Authentication
    ) : ApiResponse<StudyResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = studyRecruitmentService.createRecruitment(userId, request)
        return ApiResponseUtil.success(response)
    }

    @PutMapping("/{id}")
    fun updateStudyRecruitment(
        @PathVariable id: Long,
        @RequestBody @Valid request: StudyRequest,
        authentication: Authentication
    ): ApiResponse<StudyResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = studyRecruitmentService.updateRecruitment(userId, id, request)
        return ApiResponseUtil.success(response)
    }

    @DeleteMapping("/{id}")
    fun deleteStudyRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<StudyResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = studyRecruitmentService.deleteRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }

    @PatchMapping("/{id}/close")
    fun closeStudyRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<StudyResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = studyRecruitmentService.closeRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }

    @PatchMapping("/{id}/open")
    fun openStudyRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<StudyResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = studyRecruitmentService.openRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }
}