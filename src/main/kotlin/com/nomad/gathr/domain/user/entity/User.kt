package com.nomad.gathr.domain.user.entity

import com.nomad.gathr.common.entity.BaseEntity
import com.nomad.gathr.domain.application.entity.Application
import com.nomad.gathr.domain.participant.entity.Participant
import com.nomad.gathr.domain.recruitment.entity.Recruitment
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_username", columnList = "username"),
        Index(name = "idx_email", columnList = "email")
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var username: String,

    @Column(unique = true, nullable = false, length = 100)
    var email: String,

    @Column(nullable = false, length = 255)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Column(length = 500)
    var profileImageUrl: String? = null,

    @Column(length = 500)
    var bio: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: Role = Role.USER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var accountStatus: AccountStatus = AccountStatus.ACTIVE,

    @Column
    var lastActivatedAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL], orphanRemoval = true)
    var createdRecruitments: MutableList<Recruitment> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var participations: MutableList<Participant> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var applications: MutableList<Application> = mutableListOf()
) : BaseEntity() {

    fun updateName(newName: String) {
        if (newName.isBlank()) {
            throw IllegalArgumentException("이름은 비어있을 수 없습니다.")
        }
        name = newName
    }

    fun updateBio(newBio: String?) {
        if (newBio != null && newBio.length > 500) {
            throw IllegalArgumentException("Bio는 최대 500자까지 가능합니다.")
        }
        bio = newBio
    }

    fun updateProfileImageUrl(newProfileImageUrl: String) {
        if (newProfileImageUrl.isBlank()) {
            throw IllegalArgumentException("이미지가 정상적으로 업로드되지 않았습니다.")
        }
        profileImageUrl = newProfileImageUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        if (username != other.username) return false
        if (email != other.email) return false
        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', role=$role)"
    }
}
