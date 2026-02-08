package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface AssignmentRepository : JpaRepository<Assignment, Long> {

    // 멘티 기준 조회
    fun findAllByMentee_Id(menteeId: Long): List<Assignment>

    fun findAllByMenteeIdAndStatus(menteeId: Long, status: AssignmentStatus): List<Assignment>

    fun findAllByMenteeIdAndDueDate(menteeId: Long, dueDate: LocalDate): List<Assignment>

    fun findAllByMenteeIdAndSubject(menteeId: Long, subject: Subject): List<Assignment>

    fun findAllByMenteeIdAndSubjectAndStatus(
        menteeId: Long,
        subject: Subject,
        status: AssignmentStatus
    ): List<Assignment>

    // 멘토 기준 조회
    fun findAllByMentorId(mentorId: Long): List<Assignment>

    // 상태에 따른 과제 존재 여부 확인
    fun existsByStatusAndMentee_id(status: AssignmentStatus, menteeId: Long): Boolean

    fun existsByMentee_IdAndStatusNot(menteeId: Long, status: AssignmentStatus): Boolean

    @Query(
        value = """
        SELECT COALESCE(MAX(streak), 0)
        FROM (
            SELECT COUNT(*) as streak
            FROM (
                SELECT log_date,
                       DENSE_RANK() OVER (ORDER BY log_date) as rnk
                FROM (
                    SELECT DATE(due_date) as log_date,
                           SUM(CASE WHEN status = 'NOT_SUBMIT' THEN 1 ELSE 0 END) as not_submit_count,
                           COUNT(*) as total_count
                    FROM assignments
                    WHERE mentee_id = :menteeId
                    GROUP BY DATE(due_date)
                ) daily_status
                WHERE not_submit_count = 0 AND total_count > 0
            ) t
            GROUP BY DATE_SUB(log_date, INTERVAL rnk DAY)
        ) streaks
    """, nativeQuery = true
    )
    fun findMaxConsecutivePerfectDays(@Param("menteeId") menteeId: Long): Long
}
