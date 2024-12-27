package com.nomad.gathr.domain.recruitment.repository

import com.nomad.gathr.domain.recruitment.entity.Project
import com.nomad.gathr.domain.recruitment.entity.Study
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface CustomProjectRepository {

    fun findByFilters(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<Project>
}