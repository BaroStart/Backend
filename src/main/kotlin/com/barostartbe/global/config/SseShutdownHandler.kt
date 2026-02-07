package com.barostartbe.global.config

import com.barostartbe.global.sse.repository.SseEmitterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

private val log = KotlinLogging.logger {}

@Component
class SseShutdownHandler(
    private val sseEmitterRepository: SseEmitterRepository
) : ApplicationListener<ContextClosedEvent> {

    companion object {
        private const val SHUTDOWN = "shutdown"
    }

    override fun onApplicationEvent(event: ContextClosedEvent) {
        log.info { "애플리케이션 종료 감지, 모든 SSE 연결을 종료합니다." }

        val allEmitters = sseEmitterRepository.getAllEmitters()
        val connectionCount = allEmitters.size

        if (connectionCount > 0) {
            log.debug { "$connectionCount 개의 SSE 연결을 종료합니다." }

            allEmitters.forEach { sseEmitter ->
                try {
                    sseEmitter.send(
                        SseEmitter.event()
                            .name(SHUTDOWN)
                            .data("서버가 종료됩니다.")
                    )
                    sseEmitter.complete()
                } catch (e: Exception) {
                    log.debug { "SSE 종료 이벤트 전송 실패: ${e.message}" }
                    sseEmitter.completeWithError(e)
                }
            }
            log.info { "모든 SSE 연결이 종료되었습니다." }
        }

        sseEmitterRepository.clearAll()
    }
}