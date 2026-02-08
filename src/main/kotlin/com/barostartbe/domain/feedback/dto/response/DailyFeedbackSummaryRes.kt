package com.barostartbe.domain.feedback.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "멘티 오늘의 피드백 요약 응답 DTO")
data class DailyFeedbackSummaryRes(

    @Schema(description = "멘토 이름")
    val mentorName: String,

    @Schema(description = "피드백 요약")
    val summary: String?,

    @Schema(description = "피드백 생성 시간")
    val createdAt: LocalDateTime
)
