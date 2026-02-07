package com.barostartbe.global.sse.event

import com.barostartbe.global.sse.dto.base.SseEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class SseEventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {
    fun publishToTopic(topic: String, event: String, data: Any, correlationId: String? = null) {
        val sseEvent = SseEvent(
            topic = topic,
            event = event,
            data = data,
            timestamp = LocalDateTime.now(),
            correlationId = correlationId
        )

        eventPublisher.publishEvent(sseEvent)
        log.debug { "SSE 이벤트 발행 - 토픽: $topic, 이벤트: $event" }
    }

    fun broadcast(event: String, data: Any) {
        val sseEvent = SseEvent(
            topic = null,
            event = event,
            data = data,
            timestamp = LocalDateTime.now()
        )

        eventPublisher.publishEvent(sseEvent)
        log.debug { "SSE 브로드캐스트 이벤트 발행 - 이벤트: $event" }
    }
}
