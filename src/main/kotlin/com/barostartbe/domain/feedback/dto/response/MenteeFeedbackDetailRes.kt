package com.barostartbe.domain.feedback.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "멘티 피드백 상세 조회 응답 DTO")
data class MenteeFeedbackDetailRes(

    @Schema(description = "멘토 이름", example = "김민준 멘토")
    val mentorName: String,

    @Schema(description = "과제명", example = "이차방정식 심화 문제")
    val assignmentTitle: String,

    @Schema(description = "과제 마감일자 (과제 생성 시 설정한 마감일)", example = "2026-02-02T23:59:00")
    val assignmentDueDate: LocalDateTime,

    @Schema(description = "피드백 요약", example = "개념 이해는 좋지만 계산 실수 주의", nullable = true)
    val feedbackSummary: String?,

    @Schema(description = "피드백 내용")
    val feedbackContent: String,

    @Schema(description = "피드백 작성 시간", example = "2026-02-02T14:30:00")
    val feedbackTime: LocalDateTime
)
