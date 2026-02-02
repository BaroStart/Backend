package com.barostartbe.domain.errorlog.repository

import com.barostartbe.domain.errorlog.entity.ErrorLog
import org.springframework.data.jpa.repository.JpaRepository

interface ErrorLogRepository : JpaRepository<ErrorLog, Long>{
}
