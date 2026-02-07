package com.barostartbe.global.sse.usecase

import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.sse.repository.SseEmitterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

private val log = KotlinLogging.logger {}

@CommandUseCase
class SseSubscribeUseCase(
    private val sseEmitterRepository: SseEmitterRepository
) {

    companion object {
        const val DEFAULT_TOPIC = "general"
        private const val CONNECTED = "connected"
        private const val TIMEOUT = 60 * 60 * 1000L // 1 hour
        private const val RECONNECT_TIME = 5000L
    }

    fun subscribe(sessionId: String, topics: String?): SseEmitter {
        val topicSet = parseTopics(topics)
        val emitter = SseEmitter(TIMEOUT)

        try {
            emitter.send(
                SseEmitter.event()
                    .name(CONNECTED)
                    .reconnectTime(RECONNECT_TIME)
                    .data("SSE 연결 성공")
            )

            sseEmitterRepository.subscribe(sessionId, topicSet, emitter)
            log.debug { "SSE 구독 성공 - 세션: $sessionId, 토픽: $topicSet" }

        } catch (e: IOException) {
            log.error(e) { "SSE 연결 초기화 실패 - 세션: $sessionId" }
            emitter.completeWithError(e)
        }

        return emitter
    }

    private fun parseTopics(topics: String?): Set<String> {
        if (topics.isNullOrBlank()) {
            return setOf(DEFAULT_TOPIC)
        }

        return topics.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
    }
}
