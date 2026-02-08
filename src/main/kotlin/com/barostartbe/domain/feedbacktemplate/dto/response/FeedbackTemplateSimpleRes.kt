package com.barostartbe.domain.feedbacktemplate.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "피드백 템플릿 선택용 목록 DTO")
data class FeedbackTemplateSimpleRes(

    @Schema(description = "피드백 템플릿 ID")
    val id: Long,

    @Schema(description = "피드백 템플릿 이름")
    val name: String,

    @Schema(description = "피드백 미리보기")
    val preview: String
)
