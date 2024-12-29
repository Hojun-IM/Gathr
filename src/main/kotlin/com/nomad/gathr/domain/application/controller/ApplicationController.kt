package com.nomad.gathr.domain.application.controller

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.domain.application.dto.request.ApplicationRequest
import com.nomad.gathr.domain.application.dto.response.ApplicationResponse
import com.nomad.gathr.domain.application.dto.response.EligibilityResponse
import com.nomad.gathr.domain.application.service.ApplicationService
import com.nomad.gathr.domain.user.dto.CustomUserDetails
import com.nomad.gathr.util.ApiResponseUtil
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/application")
class ApplicationController(
    private val applicationService: ApplicationService
) {

    // 내 지원서 목록 조회
    @GetMapping("/user/{userId}")
    fun getApplicationsByUserId(
        @PathVariable userId: Long,
        authentication: Authentication
    ): ApiResponse<List<ApplicationResponse>> {
        val currentUserId = (authentication.principal as CustomUserDetails).id
        applicationService.validateAccess(currentUserId, userId)
        val response = applicationService.getApplicationsByUserId(userId)
        return ApiResponseUtil.success(response)
    }

    // 지원서 조회
    @GetMapping("/{applicationId}")
    fun getApplication(
        @PathVariable applicationId: Long,
        authentication: Authentication
    ): ApiResponse<ApplicationResponse> {
        val currentUserId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.getApplication(applicationId, currentUserId)
        return ApiResponseUtil.success(response)
    }

    // 지원서 작성
    @PostMapping
    fun createApplication(
        @RequestBody @Valid request: ApplicationRequest,
        authentication: Authentication
    ): ApiResponse<ApplicationResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.createApplication(userId, request)
        return ApiResponseUtil.success(response)
    }

    // 지원서 수정
    @PatchMapping("/{applicationId}")
    fun updateApplication(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: ApplicationRequest,
        authentication: Authentication
    ): ApiResponse<ApplicationResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.updateApplication(applicationId, userId, request)
        return ApiResponseUtil.success(response)
    }

    // 지원서 삭제
    @DeleteMapping("/{applicationId}")
    fun deleteApplication(
        @PathVariable applicationId: Long,
        authentication: Authentication
    ): ApiResponse<Unit> {
        val userId = (authentication.principal as CustomUserDetails).id
        applicationService.deleteApplication(applicationId, userId)
        return ApiResponseUtil.success(null)
    }

    // 지원서 상태 변경
    @PatchMapping("/{applicationId}/status")
    fun changeApplicationStatus(
        @PathVariable applicationId: Long,
        @RequestParam status: String,
        authentication: Authentication
    ): ApiResponse<ApplicationResponse> {
        val recruiterId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.changeApplicationStatus(applicationId, recruiterId, status)
        return ApiResponseUtil.success(response)
    }

    // 모집에 대한 지원서 목록 조회
    @GetMapping("/recruitment/{recruitmentId}")
    fun getApplicationsByRecruitmentId(
        @PathVariable recruitmentId: Long,
        authentication: Authentication
    ): ApiResponse<List<ApplicationResponse>> {
        val recruiterId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.getApplicationsByRecruitmentId(recruitmentId, recruiterId)
        return ApiResponseUtil.success(response)
    }

    // 지원서 작성 가능 여부 확인
    @GetMapping("/recruitment/{recruitmentId}/eligibility")
    fun checkApplicationEligibility(
        @PathVariable recruitmentId: Long,
        authentication: Authentication
    ): ApiResponse<EligibilityResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = applicationService.checkApplicationEligibility(userId, recruitmentId)
        return ApiResponseUtil.success(response)
    }
}