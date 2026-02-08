package com.barostartbe.domain.feedback.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "피드백 생성 요청 DTO")
data class FeedbackCreateReq(

    @Schema(description = "피드백 내용", example = "개념 이해는 좋지만 계산 과정에서 실수가 반복됩니다. 다음엔 검산 루틴을 넣어보세요.")
    @field:NotBlank
    val content: String,

    @Schema(description = "피드백 요약 (선택)", example = "전반적으로 문제 접근이 좋았습니다.")
    val summary: String?
)