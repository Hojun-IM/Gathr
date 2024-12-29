package com.nomad.gathr.domain.application.validator

import com.nomad.gathr.domain.recruitment.entity.Recruitment
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class ApplicationValidator {

    fun checkEligibility(user: User, recruitment: Recruitment): Pair<Boolean, String?> {
        if (recruitment.creator.id == user.id) {
            return false to "자신이 작성한 모집에는 지원할 수 없습니다."
        }

        if (recruitment.applications.any { it.user.id == user.id }) {
            return false to "이미 지원한 모집입니다."
        }

        if (recruitment.status != RecruitmentStatus.OPEN) {
            return false to "모집이 종료되었습니다."
        }

        return true to "지원 가능한 모집입니다."
    }

    fun validateEligibility(user: User, recruitment: Recruitment) {
        if (recruitment.creator.id == user.id) {
            throw IllegalArgumentException("자신이 작성한 모집에는 지원할 수 없습니다.")
        }

        if (!isEligible(user, recruitment)) {
            throw IllegalArgumentException("이미 지원한 모집이거나 지원 불가능한 모집입니다.")
        }
    }

    private fun isEligible(user: User, recruitment: Recruitment): Boolean {
        return recruitment.applications.none { it.user.id == user.id }
    }
}