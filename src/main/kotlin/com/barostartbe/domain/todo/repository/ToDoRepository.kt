package com.barostartbe.domain.todo.repository

import com.barostartbe.domain.todo.entity.ToDo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

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
}
