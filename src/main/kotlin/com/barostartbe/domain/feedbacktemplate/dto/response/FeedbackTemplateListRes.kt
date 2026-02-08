package com.barostartbe.domain.feedbacktemplate.dto.response

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "피드백 템플릿 목록 조회 응답 DTO")
data class FeedbackTemplateListRes(

    @Schema(description = "피드백 템플릿 ID")
    val id: Long,

    @Schema(description = "피드백 템플릿 이름")
    val name: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "피드백 템플릿 미리보기")
    val preview: String,

    @Schema(description = "생성일")
    val createdAt: LocalDateTime,

    @Schema(description = "사용 횟수")
    val usageCount: Int
) {

    companion object {

        fun from(entity: FeedbackTemplate): FeedbackTemplateListRes {
            return FeedbackTemplateListRes(
                id = entity.id!!,
                name = entity.name,
                subject = entity.subject,
                preview = entity.content.take(50),  // 목록 내 내용 미리보기
                createdAt = entity.createdAt!!,
                usageCount = entity.usageCount
            )
        }
    }
}
