package com.nomad.gathr.domain.recruitment.repository

import com.nomad.gathr.domain.recruitment.entity.Project
import com.nomad.gathr.domain.recruitment.entity.Study
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
}