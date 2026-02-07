package com.barostartbe.global.sse.usecase

import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.sse.dto.base.SseEvent
import com.barostartbe.global.sse.repository.SseEmitterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

private val log = KotlinLogging.logger {}

@CommandUseCase
class SsePublishUseCase(
    private val sseEmitterRepository: SseEmitterRepository
) {

    fun publishToTopic(topic: String, sseEvent: SseEvent) {
        if (!sseEvent.isValid()) {
            log.warn { "유효하지 않은 SSE 이벤트: $sseEvent" }
            return
        }

        val emitters = sseEmitterRepository.getTopicEmitters(topic)
        sendToEmitters(emitters, sseEvent)
    }

    fun broadcast(sseEvent: SseEvent) {
        if (!sseEvent.isValid()) {
            log.warn { "유효하지 않은 SSE 이벤트: $sseEvent" }
            return
        }

        val emitters = sseEmitterRepository.getAllEmitters()
        sendToEmitters(emitters, sseEvent)
    }

    private fun sendToEmitters(emitters: Set<SseEmitter>, sseEvent: SseEvent) {
        if (emitters.isEmpty()) return

        emitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(sseEvent.event)
                        .data(sseEvent.data)
                )
            } catch (e: Exception) {
                if (e is IOException && isClientDisconnectionError(e)) {
                    log.debug { "클라이언트 연결 끊김, Emitter 제거" }
                } else {
                    log.warn { "SSE 이벤트 전송 실패, Emitter 제거: ${e.message}" }
                }
                sseEmitterRepository.removeEmitter(emitter)
            }
        }
    }

    private fun isClientDisconnectionError(e: IOException): Boolean {
        val message = e.message
        return message != null && (
            message.contains("Broken pipe") ||
                message.contains("connection reset") ||
                message.contains("disconnected client")
            )
    }
}
