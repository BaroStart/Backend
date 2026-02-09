package com.barostartbe.global.sse.event

import com.barostartbe.global.sse.dto.base.SseEvent
import com.barostartbe.global.sse.usecase.SsePublishUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class SseEventListener(
    private val ssePublishUseCase: SsePublishUseCase
) {
    @EventListener
    fun handleSseEvent(event: SseEvent) {
        try {
            if (!event.topic.isNullOrBlank()) {
                ssePublishUseCase.publishToTopic(event.topic, event)
            } else {
                ssePublishUseCase.broadcast(event)
            }
        } catch (e: Exception) {
            log.error(e) { "SSE 이벤트 처리 실패: $event" }
        }
    }
}
