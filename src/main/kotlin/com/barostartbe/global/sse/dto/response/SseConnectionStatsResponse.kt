package com.barostartbe.global.sse.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "SSE 연결 통계 응답")
data class SseConnectionStatsResponse(
    @Schema(description = "연결된 총 세션 수", example = "5")
    val sessionCount: Int,
    @Schema(description = "현재 활성화된 총 토픽 수", example = "3")
    val topicCount: Int
)
