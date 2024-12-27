package com.nomad.gathr.domain.recruitment.repository

import com.nomad.gathr.domain.recruitment.entity.*
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class CustomProjectRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomProjectRepository{

    override fun findByFilters(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<Project> {

        val project = QProject.project

        // 동적 조건 생성 (Null이 아닌 조건만 필터링)
        val conditions = listOfNotNull(
            title?.let { project.title.containsIgnoreCase(it) },
            status?.let {
                runCatching { RecruitmentStatus.valueOf(it) }
                    .getOrNull()
                    ?.let { status -> project.status.eq(status) }
            },
            startDate?.let { project.startDate.goe(it) },
            endDate?.let { project.endDate.loe(it) },
            locationType?.let {
                runCatching { LocationType.valueOf(it) }
                    .getOrNull()
                    ?.let { locType -> project.locationType.eq(locType) }
            }
        ).reduceOrNull { acc, expr -> acc.and(expr) }

        // 쿼리 실행
        val content = queryFactory
            .selectFrom(project)
            .where(conditions)
            .applySorting(pageable)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 데이터 개수 쿼리
        val totalCount = queryFactory
            .select(project.count())
            .from(project)
            .where(conditions)
            .fetchOne() ?: 0

        return PageImpl(content, pageable, totalCount)
    }

    // QueryDSL 정렬 처리용 확장 함수 (pageable.sort에 정의된 조건들을 순회하며 orderBy에 적용)
    private fun <T> JPQLQuery<T>.applySorting(pageable: Pageable): JPQLQuery<T> {
        pageable.sort.forEach { order ->
            val property = order.property

            // QProject.project에서 정렬 대상 Path를 가져옴
            val sortPath = when (property) {
                "title" -> QProject.project.title
                "startDate" -> QProject.project.startDate
                "endDate" -> QProject.project.endDate
                "status" -> QProject.project.status
                else -> QProject.project.id // 기본값
            }

            this.orderBy(
                if (order.isAscending) sortPath.asc() else sortPath.desc()
            )
        }
        return this
    }
}