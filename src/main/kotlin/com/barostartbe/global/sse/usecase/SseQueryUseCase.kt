package com.barostartbe.global.sse.usecase

import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.sse.dto.response.SseConnectionStatsResponse
import com.barostartbe.global.sse.repository.SseEmitterRepository

@QueryUseCase
class SseQueryUseCase(
    private val sseEmitterRepository: SseEmitterRepository
) {
    fun getConnectionStats(): SseConnectionStatsResponse {
        return SseConnectionStatsResponse(
            sessionCount = sseEmitterRepository.getConnectedSessionCount(),
            topicCount = sseEmitterRepository.getTopicCount()
        )
    }
}
