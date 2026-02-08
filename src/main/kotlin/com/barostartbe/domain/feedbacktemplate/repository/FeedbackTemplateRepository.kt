package com.barostartbe.domain.feedbacktemplate.repository

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FeedbackTemplateRepository : JpaRepository<FeedbackTemplate, Long> {

    // 과목 기준 템플릿 목록 조회 (최신순)
    fun findAllBySubjectOrderByCreatedAtDesc(subject: Subject): List<FeedbackTemplate>

    // 전체 템플릿 목록 조회 (최신순)
    fun findAllByOrderByCreatedAtDesc(): List<FeedbackTemplate>

    // 템플릿 이름 검색
    @Query(
        """
        select ft
        from FeedbackTemplate ft
        where ft.name like concat('%', :keyword, '%')
        order by ft.createdAt desc
        """
    )
    fun searchByName(@Param("keyword") keyword: String): List<FeedbackTemplate>
}
