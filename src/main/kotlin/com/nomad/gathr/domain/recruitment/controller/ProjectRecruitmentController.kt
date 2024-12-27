package com.nomad.gathr.domain.recruitment.controller

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.domain.recruitment.dto.request.ProjectRequest
import com.nomad.gathr.domain.recruitment.dto.response.ProjectResponse
import com.nomad.gathr.domain.recruitment.service.ProjectRecruitmentService
import com.nomad.gathr.domain.user.dto.CustomUserDetails
import com.nomad.gathr.util.ApiResponseUtil
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/recruitment/project")
class ProjectRecruitmentController(
    private val projectRecruitmentService: ProjectRecruitmentService
) {

    @GetMapping("/{id}")
    fun getProjectRecruitment(
        @PathVariable id: Long
    ): ApiResponse<ProjectResponse> {
        val response = ProjectRecruitmentService.getRecruitment(id)
        return ApiResponseUtil.success(response)
    }

    @GetMapping("/list")
    fun getProjectRecruitments(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(required = false) locationType: String?,
        pageable: Pageable
    ): ApiResponse<Page<ProjectResponse>> {
        val response = ProjectRecruitmentService.getRecruitments(
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
    fun createProjectRecruitment(
        @RequestBody @Valid request: ProjectRequest,
        authentication: Authentication
    ) : ApiResponse<ProjectResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = ProjectRecruitmentService.createRecruitment(userId, request)
        return ApiResponseUtil.success(response)
    }

    @PutMapping("/{id}")
    fun updateProjectRecruitment(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProjectRequest,
        authentication: Authentication
    ): ApiResponse<ProjectResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = ProjectRecruitmentService.updateRecruitment(userId, id, request)
        return ApiResponseUtil.success(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProjectRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<ProjectResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = ProjectRecruitmentService.deleteRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }

    @PatchMapping("/{id}/close")
    fun closeProjectRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<ProjectResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = ProjectRecruitmentService.closeRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }

    @PatchMapping("/{id}/open")
    fun openProjectRecruitment(
        @PathVariable id: Long,
        authentication: Authentication
    ): ApiResponse<ProjectResponse> {
        val userId = (authentication.principal as CustomUserDetails).id
        val response = ProjectRecruitmentService.openRecruitment(userId, id)
        return ApiResponseUtil.success(response)
    }
}