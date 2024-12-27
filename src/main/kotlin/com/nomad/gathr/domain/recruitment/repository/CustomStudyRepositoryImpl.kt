package com.nomad.gathr.domain.recruitment.repository

import com.nomad.gathr.domain.recruitment.entity.LocationType
import com.nomad.gathr.domain.recruitment.entity.QStudy
import com.nomad.gathr.domain.recruitment.entity.RecruitmentStatus
import com.nomad.gathr.domain.recruitment.entity.Study
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class CustomStudyRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomStudyRepository{

    override fun findByFilters(
        title: String?,
        status: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        locationType: String?,
        pageable: Pageable
    ): Page<Study> {

        val study = QStudy.study

        // 동적 조건 생성 (Null이 아닌 조건만 필터링)
        val conditions = listOfNotNull(
            title?.let { study.title.containsIgnoreCase(it) },
            status?.let {
                runCatching { RecruitmentStatus.valueOf(it) }
                    .getOrNull()
                    ?.let { status -> study.status.eq(status) }
            },
            startDate?.let { study.startDate.goe(it) },
            endDate?.let { study.endDate.loe(it) },
            locationType?.let {
                runCatching { LocationType.valueOf(it) }
                    .getOrNull()
                    ?.let { locType -> study.locationType.eq(locType) }
            }
        ).reduceOrNull { acc, expr -> acc.and(expr) }

        // 쿼리 실행
        val content = queryFactory
            .selectFrom(study)
            .where(conditions)
            .applySorting(pageable)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 데이터 개수 쿼리
        val totalCount = queryFactory
            .select(study.count())
            .from(study)
            .where(conditions)
            .fetchOne() ?: 0

        return PageImpl(content, pageable, totalCount)
    }

    // QueryDSL 정렬 처리용 확장 함수 (pageable.sort에 정의된 조건들을 순회하며 orderBy에 적용)
    private fun <T> JPQLQuery<T>.applySorting(pageable: Pageable): JPQLQuery<T> {
        pageable.sort.forEach { order ->
            val property = order.property

            // QStudy.study에서 정렬 대상 Path를 가져옴
            val sortPath = when (property) {
                "title" -> QStudy.study.title
                "startDate" -> QStudy.study.startDate
                "endDate" -> QStudy.study.endDate
                "status" -> QStudy.study.status
                else -> QStudy.study.id // 기본값
            }

            this.orderBy(
                if (order.isAscending) sortPath.asc() else sortPath.desc()
            )
        }
        return this
    }
}