package com.barostartbe.global.sse.usecase

import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.sse.repository.SseEmitterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

private val log = KotlinLogging.logger {}

@CommandUseCase
class SseHeartbeatUseCase(
    private val sseEmitterRepository: SseEmitterRepository
) {

    companion object {
        private const val PING = "ping"
    }

    @Scheduled(fixedDelay = 30 * 1000)
    fun sendHeartbeat() {
        val allEmitters = sseEmitterRepository.getAllEmitters()
        if (allEmitters.isEmpty()) return

        allEmitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(PING)
                        .data("heartbeat")
                )
            } catch (e: Exception) {
                if (e is IOException && isClientDisconnectionError(e)) {
                    log.debug { "클라이언트 연결 끊김으로 인한 하트비트 전송 실패" }
                } else {
                    log.warn { "하트비트 전송 실패, Emitter 제거: ${e.message}" }
                }
                sseEmitterRepository.removeEmitter(emitter)
            }
        }

        log.debug { "${allEmitters.size}개의 연결에 하트비트 전송 완료" }
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
