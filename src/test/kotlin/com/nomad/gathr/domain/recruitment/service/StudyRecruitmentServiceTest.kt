package com.nomad.gathr.domain.recruitment.service

import com.nomad.gathr.domain.recruitment.dto.request.StudyRequest
import com.nomad.gathr.domain.recruitment.dto.response.StudyResponse
import com.nomad.gathr.domain.recruitment.entity.*
import com.nomad.gathr.domain.recruitment.mapper.StudyRecruitmentMapper
import com.nomad.gathr.domain.recruitment.repository.StudyRepository
import com.nomad.gathr.domain.recruitment.validator.RecruitmentValidator
import com.nomad.gathr.domain.user.entity.AccountStatus
import com.nomad.gathr.domain.user.entity.AuthProvider
import com.nomad.gathr.domain.user.entity.Role
import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.domain.user.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class StudyRecruitmentServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val studyRepository = mockk<StudyRepository>()
    private val recruitmentValidator = mockk<RecruitmentValidator>(relaxed = true)
    private val studyRecruitmentMapper = mockk<StudyRecruitmentMapper>()
    private val studyRecruitmentService = StudyRecruitmentService(
        userRepository = userRepository,
        studyRepository = studyRepository,
        recruitmentValidator = recruitmentValidator,
        studyRecruitmentMapper = studyRecruitmentMapper
    )

    private val userId: Long = 100L
    private val user = User(
        id = userId,
        username = "test1234",
        email = "test@email.com",
        password = "Password123",
        name = "Test User",
        role = Role.USER,
        accountStatus = AccountStatus.ACTIVE,
        provider = AuthProvider.LOCAL
    )

    private val validRequest = StudyRequest(
        topic = StudyTopic.ETC,
        goal = "Learn Spring Boot",
        title = "Spring Study",
        description = "Study about Spring Boot and JPA",
        startDate = LocalDate.now().plusDays(1),
        endDate = LocalDate.now().plusDays(10),
        maxParticipants = 5,
        locationType = LocationType.ONLINE,
        location = null,
        link = "https://example.com"
    )

    @Test
    @DisplayName("[SUCCESS] 스터디 모집 생성")
    fun createRecruitment_Success() {
        // given
        val currentTime = LocalDateTime.of(2024, 12, 22, 0, 0)

        val newStudy = Study(
            creator = user,
            title = validRequest.title,
            description = validRequest.description,
            startDate = validRequest.startDate,
            endDate = validRequest.endDate,
            maxParticipants = validRequest.maxParticipants,
            locationType = validRequest.locationType,
            location = validRequest.location,
            link = validRequest.link,
            status = RecruitmentStatus.OPEN,
            topic = validRequest.topic,
            goal = validRequest.goal
        )

        val savedStudy = newStudy.apply {
            javaClass.getDeclaredField("id").let {
                it.isAccessible = true
                it.set(this, 1L)
            }
        }

        // Mock 동작 정의
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { studyRecruitmentMapper.toEntity(validRequest, user) } returns newStudy
        every { studyRepository.save(any()) } answers { arg<Study>(0).apply {
            javaClass.getDeclaredField("id").let {
                it.isAccessible = true
                it.set(this, 1L)
            }
        }}
        every { studyRecruitmentMapper.toResponse(savedStudy) } returns StudyResponse(
            id = 1L,
            topic = validRequest.topic,
            goal = validRequest.goal,
            creatorId = userId,
            creatorName = user.name,
            title = validRequest.title,
            description = validRequest.description,
            startDate = validRequest.startDate,
            endDate = validRequest.endDate,
            currentParticipants = 0, // 적절히 설정
            maxParticipants = validRequest.maxParticipants,
            locationType = validRequest.locationType,
            location = validRequest.location,
            link = validRequest.link,
            status = RecruitmentStatus.OPEN,
            createdAt = currentTime,
            updatedAt = null,
            recruitmentType = RecruitmentType.STUDY
        )

        // when
        val result = studyRecruitmentService.createRecruitment(userId, validRequest)

        // then
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals(validRequest.title, result.title)
        assertEquals(RecruitmentStatus.OPEN, result.status)
        assertEquals(currentTime, result.createdAt)

        // Validator 호출 검증
        verify(exactly = 1) { recruitmentValidator.validateDate(validRequest.startDate, validRequest.endDate) }

        // Repository 및 Mapper 호출 검증
        verify { userRepository.findById(userId) }
        verify { studyRepository.save(newStudy) }
        verify { studyRecruitmentMapper.toResponse(savedStudy) }
    }













}
