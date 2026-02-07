package com.barostartbe.domain.user.repository

import com.barostartbe.domain.user.entity.AccessLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccessLogRepository : JpaRepository<AccessLog, Long> {
    fun findFirstByUserIdOrderByCreatedAtDesc(userId: Long): AccessLog?

    @Query(value = """
        SELECT COALESCE(MAX(streak), 0)
        FROM (
            SELECT COUNT(*) as streak
            FROM (
                SELECT DISTINCT DATE(created_at) as log_date,
                       DENSE_RANK() OVER (ORDER BY DATE(created_at)) as rnk
                FROM access_logs
                WHERE user_id = :userId
            ) t
            GROUP BY DATE_SUB(log_date, INTERVAL rnk DAY)
        ) streaks
    """, nativeQuery = true)
    fun findMaxConsecutiveDays(@Param("userId") userId: Long): Int
}