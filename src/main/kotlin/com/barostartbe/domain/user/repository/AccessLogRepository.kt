package com.barostartbe.domain.user.repository

import com.barostartbe.domain.user.entity.AccessLog
import org.springframework.data.jpa.repository.JpaRepository

interface AccessLogRepository : JpaRepository<AccessLog, Long> {
    fun findFirstByUserIdOrderByCreatedAtDesc(userId: Long): AccessLog?
}