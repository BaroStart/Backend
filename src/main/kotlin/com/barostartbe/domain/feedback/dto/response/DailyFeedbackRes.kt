package com.barostartbe.domain.feedback.dto.response

import com.barostartbe.domain.assignment.entity.enums.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "멘티 기준 일별 피드백 카드 응답 DTO")
data class DailyFeedbackRes(

    @Schema(description = "과목명", example = "MATH")
    val subject: Subject,

    @Schema(description = "피드백 작성 멘토 이름", example = "김민준")
    val mentorName: String,

    @Schema(description = "피드백 작성 시각", example = "2026-02-02T11:05:00")
    val feedbackTime: LocalDateTime,

    @Schema(description = "피드백 본문 내용", example = "문단별 요약이 좋아요. 다만 문장 표시를 한 번 더 해보면 정확도가 더 올라갑니다.")
    val content: String,

    @Schema(description = "피드백이 연결된 과제 ID (과제 상세 조회용)", example = "12")
    val assignmentId: Long
)
