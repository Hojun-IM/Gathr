package com.nomad.gathr.domain.user.controller

import com.nomad.gathr.common.dto.ApiResponse
import com.nomad.gathr.domain.user.dto.*
import com.nomad.gathr.domain.user.dto.request.GeneratePreSignedUrlRequest
import com.nomad.gathr.domain.user.dto.request.UpdateUserPasswordRequest
import com.nomad.gathr.domain.user.dto.request.UpdateUserProfileRequest
import com.nomad.gathr.domain.user.dto.request.UpdateUserProfileUrlRequest
import com.nomad.gathr.domain.user.dto.response.GeneratePreSignedUrlResponse
import com.nomad.gathr.domain.user.dto.response.UserProfileHeaderResponse
import com.nomad.gathr.domain.user.dto.response.UserProfileResponse
import com.nomad.gathr.domain.user.service.UserService
import com.nomad.gathr.util.ApiResponseUtil
import com.nomad.gathr.util.S3Utils
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val s3Utils: S3Utils
) {

    @GetMapping("/me")
    fun getUserProfile(authentication: Authentication): ApiResponse<UserProfileResponse> {
        val userProfile = userService.getUserProfile((authentication.principal as CustomUserDetails).id)
        return ApiResponseUtil.success(userProfile)
    }

    @PutMapping("/me")
    fun updateUserProfile(
        @RequestBody @Valid request: UpdateUserProfileRequest,
        authentication: Authentication
    ): ApiResponse<UserProfileResponse> {
        val userProfile = userService.updateUserProfile((authentication.principal as CustomUserDetails).id, request)
        return ApiResponseUtil.success(userProfile)
    }

    /** 프로필 이미지 업로드를 위한 Pre-Signed URL 생성 **/
    @PatchMapping("/pre-signed-url")
    fun generateUploadUrl(
        @RequestBody request: GeneratePreSignedUrlRequest,
        authentication: Authentication
    ): ApiResponse<GeneratePreSignedUrlResponse> {
        val (url, filePath) = s3Utils.generatePreSignedUrl(request.fileName, request.fileType)
        return ApiResponseUtil.success(GeneratePreSignedUrlResponse(url, filePath))
    }

    /** 프로필 이미지 URL 업데이트 **/
    @PatchMapping("/me/profile-image")
    fun updateUserProfileImage(
        @RequestBody request: UpdateUserProfileUrlRequest,
        authentication: Authentication
    ): ApiResponse<UserProfileResponse> {
        val userProfile = userService.updateUserProfileUrl((authentication.principal as CustomUserDetails).id, request)
        return ApiResponseUtil.success(userProfile)
    }

    @PatchMapping("/me/password")
    fun updateUserPassword(
        @RequestBody @Valid request: UpdateUserPasswordRequest,
        authentication: Authentication
    ): ApiResponse<Nothing> {
        userService.updateUserPassword((authentication.principal as CustomUserDetails).id, request)
        return ApiResponseUtil.success(null, "비밀번호가 변경되었습니다.")
    }

    @GetMapping("/profile")
    fun getUserProfileForHeader(
        authentication: Authentication
    ) : ApiResponse<UserProfileHeaderResponse>{
        val userProfile = userService.getUserProfileForHeader((authentication.principal as CustomUserDetails).id)
        return ApiResponseUtil.success(userProfile)
    }
}