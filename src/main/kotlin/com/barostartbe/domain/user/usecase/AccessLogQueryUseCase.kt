package com.barostartbe.domain.user.usecase

import com.barostartbe.domain.user.repository.AccessLogRepository
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AccessLogQueryUseCase(
    private val accessLogRepository: AccessLogRepository
) {
    fun getMaxConsecutiveDays(menteeId: Long): Long = accessLogRepository.findMaxConsecutiveDays(menteeId)
}
