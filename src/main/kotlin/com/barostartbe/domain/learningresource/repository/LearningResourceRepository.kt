package com.barostartbe.domain.learningresource.repository

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.learningresource.entity.LearningResource
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository

interface LearningResourceRepository : JpaRepository<LearningResource, Long> {

    // 멘토 본인 학습 자료 목록 조회 (최신 등록순)
    fun findAllByMentorOrderByCreatedAtDesc(mentor: Mentor): List<LearningResource>

    // 멘토 본인 과목 기준 학습 자료 조회
    fun findAllByMentorAndSubjectOrderByCreatedAtDesc(
        mentor: Mentor,
        subject: Subject
    ): List<LearningResource>

    // 과제 템플릿 선택용 (여러 개 선택)
    fun findAllByMentorAndIdIn(
        mentor: Mentor,
        ids: List<Long>
    ): List<LearningResource>
}
