package com.nomad.gathr.domain.application.repository

import com.nomad.gathr.domain.application.entity.Application
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationRepository : JpaRepository<Application, Long> {
    fun findByUserId(userId: Long): List<Application>
    fun findByRecruitmentId(recruitmentId: Long): List<Application>
}