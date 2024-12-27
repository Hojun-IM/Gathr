package com.nomad.gathr.domain.recruitment.service

import com.nomad.gathr.domain.recruitment.dto.request.RecruitmentRequest
import com.nomad.gathr.domain.recruitment.dto.response.RecruitmentResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface RecruitmentService<REQ : RecruitmentRequest, RES : RecruitmentResponse> {
    // 조회
    fun getRecruitment(id: Long): RES
    fun getRecruitments(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<RES>

    // 생성, 수정, 삭제
    fun createRecruitment(userId: Long, request: REQ): RES
    fun updateRecruitment(userId: Long, id: Long, request: REQ): RES
    fun deleteRecruitment(userId: Long, id: Long): RES

    // 모집 시작, 종료
    fun closeRecruitment(userId: Long, id: Long): RES
    fun openRecruitment(userId: Long, id: Long): RES
}