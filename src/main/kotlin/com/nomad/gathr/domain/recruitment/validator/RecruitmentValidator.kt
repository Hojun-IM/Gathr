package com.nomad.gathr.domain.recruitment.validator

import com.nomad.gathr.execption.constant.ErrorCode
import com.nomad.gathr.execption.handler.CustomException
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RecruitmentValidator {
    fun validateDate(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) {
            throw CustomException(ErrorCode.RECRUITMENT_DATE_INVALID)
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw CustomException(ErrorCode.RECRUITMENT_DATE_INVALID)
        }
    }

    fun validateOpenDate(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw CustomException(ErrorCode.RECRUITMENT_DATE_INVALID)
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw CustomException(ErrorCode.RECRUITMENT_DATE_INVALID)
        }
    }

    fun validateCurrentParticipant(currentParticipant: Int, maxParticipants: Int) {
        if (currentParticipant > maxParticipants) {
            throw CustomException(ErrorCode.RECRUITMENT_PARTICIPANT_MAX)
        }
    }
}