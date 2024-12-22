package com.nomad.gathr.domain.user.service

import com.nomad.gathr.domain.user.dto.request.UpdateUserPasswordRequest
import com.nomad.gathr.domain.user.dto.request.UpdateUserProfileRequest
import com.nomad.gathr.domain.user.dto.request.UpdateUserProfileUrlRequest
import com.nomad.gathr.domain.user.dto.response.UserProfileHeaderResponse
import com.nomad.gathr.domain.user.dto.response.UserProfileResponse
import com.nomad.gathr.domain.user.repository.UserRepository
import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import lombok.extern.slf4j.Slf4j
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun getUserProfile(userId: Long): UserProfileResponse {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        return UserProfileResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            name = user.name,
            profileImageUrl = user.profileImageUrl,
            bio = user.bio
        )
    }

    fun getUserProfileForHeader(userId: Long): UserProfileHeaderResponse {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        return UserProfileHeaderResponse(
            userId = user.id!!,
            username = user.username,
            name = user.name,
            profileImageUrl = user.profileImageUrl
        )
    }

    @Transactional
    fun updateUserProfile(userId: Long, request: UpdateUserProfileRequest): UserProfileResponse {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        user.updateName(request.name)
        user.updateBio(request.bio)
        return getUserProfile(userId)
    }

    @Transactional
    fun updateUserProfileUrl(userId: Long, request: UpdateUserProfileUrlRequest): UserProfileResponse {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        user.updateProfileImageUrl(request.profileImageUrl)
        return getUserProfile(userId)
    }

    @Transactional
    fun updateUserPassword(userId: Long, request: UpdateUserPasswordRequest) {
        val user = userRepository.findById(userId).orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw CustomException(ErrorCode.SIGNIN_INPUT_INVALID)
        }

        user.password = passwordEncoder.encode(request.newPassword)
    }
}