package com.nomad.gathr.domain.recruitment.entity

import com.nomad.gathr.common.entity.BaseEntity
import com.nomad.gathr.domain.application.entity.Application
import com.nomad.gathr.domain.participant.entity.Participant
import com.nomad.gathr.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "recruitment")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "recruitment_type", discriminatorType = DiscriminatorType.STRING)
abstract class Recruitment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: User,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(length = 2000)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = false, updatable = false, name = "recruitment_type")
    var recruitmentType: RecruitmentType,

    @Enumerated(EnumType.STRING)
    var requirement: Requirement = Requirement.NONE,

    @Column(nullable = false, name = "start_date")
    var startDate: LocalDate,

    @Column(nullable = false, name = "end_date")
    var endDate: LocalDate,

    @Column(nullable = false, name = "max_participants")
    var maxParticipants: Int,

    @Column(nullable = false, name = "current_participants")
    var currentParticipants: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var locationType: LocationType,

    @Column(nullable = true, length = 500)
    var location: String? = null,

    @Column(nullable = true, length = 500)
    var link: String? = null, // 참여한 사람들에게 보여줄 링크

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: RecruitmentStatus = RecruitmentStatus.OPEN,

    @OneToMany(mappedBy = "recruitment", cascade = [CascadeType.ALL], orphanRemoval = true)
    val participants: MutableList<Participant> = mutableListOf(),

    @OneToMany(mappedBy = "recruitment", cascade = [CascadeType.ALL], orphanRemoval = true)
    val applications: MutableList<Application> = mutableListOf()
) : BaseEntity()