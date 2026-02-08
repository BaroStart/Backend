package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.Assignment
import org.springframework.data.jpa.repository.JpaRepository

interface AssignmentRepository : JpaRepository<Assignment, Long> {

    // 멘티 기준 조회
    fun findAllByMentee_Id(menteeId: Long): List<Assignment>

    // 멘토 기준 조회
    fun findAllByMentorId(mentorId: Long): List<Assignment>
}
