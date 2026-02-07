package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface AssignmentRepository : JpaRepository<Assignment, Long> {

    // 멘티 기준 조회
    fun findAllByMentee_Id(menteeId: Long): List<Assignment>

    fun findAllByMenteeIdAndStatus(menteeId: Long, status: AssignmentStatus): List<Assignment>

    fun findAllByMenteeIdAndDueDate(menteeId: Long, dueDate: LocalDate): List<Assignment>

    fun findAllByMenteeIdAndSubject(menteeId: Long, subject: Subject): List<Assignment>

    fun findAllByMenteeIdAndSubjectAndStatus(menteeId: Long, subject: Subject, status: AssignmentStatus): List<Assignment>

    // 멘토 기준 조회
    fun findAllByMentorId(mentorId: Long): List<Assignment>

    // 상태에 따른 과제 존재 여부 확인
    fun existsByStatusAndMentee_id(status: AssignmentStatus, menteeId: Long): Boolean
}
