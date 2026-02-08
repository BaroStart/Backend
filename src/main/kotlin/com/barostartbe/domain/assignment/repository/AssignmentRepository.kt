package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface AssignmentRepository : JpaRepository<Assignment, Long> {

    // 멘티 기준 조회
    fun findAllByMentee_Id(menteeId: Long): List<Assignment>

    // 멘토 기준 조회
    fun findAllByMentorId(mentorId: Long): List<Assignment>

    fun existsByMentee_IdAndStatusNot(menteeId: Long, status: AssignmentStatus): Boolean

    // 멘토 ID, 상태 목록 기준 최신 제출일 내림차순 조회
    fun findAllByMentorIdAndStatusInOrderBySubmittedAtDesc(mentorId: Long, status: List<AssignmentStatus>): List<Assignment>

    @Query(
        """
        SELECT COUNT(a)
        FROM Assignment a
        WHERE a.mentee.id = :menteeId
            AND a.status IN ('SUBMITTED', 'FEEDBACKED')
            AND FUNCTION('TIMESTAMPDIFF', MINUTE, a.startTime, a.endTime) >= 25
        """
    )
    fun countCompletedOver25Minutes(@Param("menteeId") menteeId: Long): Long

    @Query(
        """
        SELECT COUNT(a)
        FROM Assignment a
        WHERE a.mentee.id = :menteeId
            AND a.status IN ('SUBMITTED', 'FEEDBACKED')
            AND FUNCTION('TIME', a.startTime) >= '06:00:00'
            AND FUNCTION('TIME', a.endTime) <= '09:00:00'
            AND FUNCTION('DATE', a.startTime) = FUNCTION('DATE', a.endTime)
        """
    )
    fun countStudyBetweenSixAndNine(@Param("menteeId") menteeId: Long): Long

    @Query(
        """
        SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, a.startTime, a.endTime))
        FROM Assignment a
        WHERE a.mentee.id = :menteeId
            AND a.status != com.barostartbe.domain.assignment.entity.enums.AssignmentStatus.NOT_SUBMIT
            AND (:subject IS NULL OR a.subject = :subject)
        """
    )
    fun sumStudyTimeByMenteeId(@Param("menteeId") menteeId: Long, @Param("subject") subject: Subject?): Long?

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
    fun findMaxConsecutivePerfectDays(@Param("menteeId") menteeId: Long): Int

    @Query("SELECT a FROM Assignment a WHERE a.mentee.id = :menteeId AND a.status != 'NOT_SUBMIT' AND DATE(a.startTime) = :date")
    fun findAllByMenteeIdAndStartTimeDate(
        @Param("menteeId") menteeId: Long,
        @Param("date") date: LocalDate
    ): List<Assignment>

    @Query("SELECT a FROM Assignment a WHERE a.mentee.id = :menteeId AND DATE(a.createdAt) <= :endDate AND DATE(a.dueDate) >= :startDate")
    fun findAllByMenteeIdAndDateOverlapping(
        @Param("menteeId") menteeId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Assignment>

    @Query(
        """
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Assignment a
        WHERE a.mentee.id = :menteeId
            AND a.startTime >= :startTime
            AND a.endTime <= :endTime
        """
    )
    fun existsByMenteeIdAndTimeRange(
        @Param("menteeId") menteeId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): Boolean
}
