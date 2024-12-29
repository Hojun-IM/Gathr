package com.nomad.gathr.domain.application.entity

import com.nomad.gathr.common.entity.BaseEntity
import com.nomad.gathr.domain.recruitment.entity.Recruitment
import com.nomad.gathr.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "application")
class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruitment_id", nullable = false)
    val recruitment: Recruitment,

    @Column(nullable = false)
    var status: ApplicationStatus = ApplicationStatus.PENDING,

    @Column(nullable = false)
    var applicationMessage: String? = null
) : BaseEntity()