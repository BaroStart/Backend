package com.barostartbe.global.sse.dto.base

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class SseEvent(
    val topic: String?,
    val event: String,
    val data: Any,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val correlationId: String? = null
) {
    fun isValid(): Boolean {
        return event.isNotBlank()
    }
}
