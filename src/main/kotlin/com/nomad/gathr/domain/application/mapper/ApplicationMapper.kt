package com.nomad.gathr.domain.application.mapper

import com.nomad.gathr.domain.application.dto.request.ApplicationRequest
import com.nomad.gathr.domain.application.dto.response.ApplicationResponse
import com.nomad.gathr.domain.application.entity.Application
import com.nomad.gathr.domain.recruitment.entity.Recruitment
import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import org.springframework.stereotype.Component

@Component
class ApplicationMapper {

    fun toEntity(
        request: ApplicationRequest,
        user: User,
        recruitment: Recruitment) : Application {

        return Application(
            user = user,
            recruitment = recruitment,
            applicationMessage = request.applicationMessage
        )
    }

    fun toResponse(application: Application) : ApplicationResponse {
        return ApplicationResponse(
            id = application.id ?: throw CustomException(ErrorCode.APPLICATION_NOT_FOUND),
            userId = application.user.id ?: throw CustomException(ErrorCode.USER_NOT_FOUND),
            name = application.user.name,
            recruitmentId = application.recruitment.id ?: throw CustomException(ErrorCode.RECRUITMENT_NOT_FOUND),
            recruitmentTitle = application.recruitment.title,
            status = application.status,
            applicationMessage = application.applicationMessage?:""
        )
    }
}