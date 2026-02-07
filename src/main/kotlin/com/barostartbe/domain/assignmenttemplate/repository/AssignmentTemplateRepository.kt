package com.barostartbe.domain.assignmenttemplate.repository

import com.barostartbe.domain.assignment.entity.AssignmentGoal
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository

interface AssignmentTemplateRepository : JpaRepository<AssignmentTemplate, Long> {

    // 멘토 본인이 생성한 모든 과제 템플릿 조회 (최신순)
    fun findAllByMentorOrderByCreatedAtDesc(mentor: Mentor): List<AssignmentTemplate>

    // 멘토 본인이 생성한 모든 과제 템플릿 과목별 조회
    fun findAllByMentorAndSubjectOrderByCreatedAtDesc(mentor: Mentor, subject: Subject): List<AssignmentTemplate>

    // 멘토 본인 소유의 특정 템플릿 단건 조회
    fun findByIdAndMentor(id: Long, mentor: Mentor): AssignmentTemplate?

    // 특정 과제 목표에 연결된 모든 과제 템플릿 조회 (최신순)
    fun findAllByAssignmentGoalOrderByCreatedAtDesc(goal: AssignmentGoal): List<AssignmentTemplate>

}
