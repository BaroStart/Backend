package com.barostartbe.domain.todo.dto.base

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class TimeSlot(

    @Schema(description = "시작 시간", example = "2026-02-05T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    val startTime: LocalDateTime,

    @Schema(description = "종료 시간", example = "2026-02-05T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    val endTime: LocalDateTime
)
