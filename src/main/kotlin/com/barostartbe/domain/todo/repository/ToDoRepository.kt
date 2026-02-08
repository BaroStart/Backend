package com.barostartbe.domain.todo.repository

import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface ToDoRepository : JpaRepository<ToDo, Long> {

    @Query(
        """
        SELECT t
        FROM ToDo t
        WHERE t.mentee.id = :menteeId 
            AND DATE(t.createdAt) = :date
        """
    )
    fun findAllByMenteeIdAndCreatedDate(
        @Param("menteeId") menteeId: Long,
        @Param("date") date: LocalDate
    ): List<ToDo>

    fun findAllByMentee_IdAndStatus(menteeId: Long, status: Status): List<ToDo>

    @Query(
        """
        SELECT COUNT(t)
        FROM ToDo t
        WHERE t.mentee.id = :menteeId
            AND t.status = 'COMPLETED'
            AND FUNCTION('TIMESTAMPDIFF', MINUTE, t.startTime, t.endTime) >= 25
        """
    )
    fun countCompletedOver25Minutes(@Param("menteeId") menteeId: Long): Long

    @Query(
        """
        SELECT COUNT(t)
        FROM ToDo t
        WHERE t.mentee.id = :menteeId
            AND t.status = 'COMPLETED'
            AND FUNCTION('TIME', t.startTime) >= '06:00:00'
            AND FUNCTION('TIME', t.endTime) <= '09:00:00'
            AND FUNCTION('DATE', t.startTime) = FUNCTION('DATE', t.endTime)
        """
    )
    fun countStudyBetweenSixAndNine(@Param("menteeId") menteeId: Long): Long

    @Query(value = """
        SELECT COALESCE(MAX(streak), 0)
        FROM (
            SELECT COUNT(*) as streak
            FROM (
                SELECT log_date,
                       DENSE_RANK() OVER (ORDER BY log_date) as rnk
                FROM (
                    SELECT DATE(created_at) as log_date,
                           SUM(CASE WHEN status = 'NOT_COMPLETED' THEN 1 ELSE 0 END) as not_completed_count,
                           COUNT(*) as total_count
                    FROM todos
                    WHERE mentee_id = :menteeId
                    GROUP BY DATE(created_at)
                ) daily_status
                WHERE not_completed_count = 0 AND total_count > 0
            ) t
            GROUP BY DATE_SUB(log_date, INTERVAL rnk DAY)
        ) streaks
    """, nativeQuery = true)
    fun findMaxConsecutivePerfectDays(@Param("menteeId") menteeId: Long): Long

    @Query(
        """
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM ToDo t
        WHERE t.mentee.id = :menteeId
            AND t.startTime >= :startTime
            AND t.endTime <= :endTime
    """
    )
    fun existsByMenteeIdAndTimeRange(
        @Param("menteeId") menteeId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): Boolean
}
