package com.barostartbe.domain.feedbacktemplate.dto.request

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "피드백 템플릿 수정 요청 DTO")
data class FeedbackTemplateUpdateReq(

    @Schema(description = "피드백 템플릿 이름", example = "국어 서술형 피드백(개선)")
    @field:NotBlank
    val name: String,

    @Schema(description = "과목", example = "KOREAN")
    @field:NotNull
    val subject: Subject,

    @Schema(description = "피드백 템플릿 본문", example = "문장 구조가 전반적으로 안정적입니다...")
    @field:NotBlank
    val content: String
)
