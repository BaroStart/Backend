package com.barostartbe.domain.feedbacktemplate.dto.response

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "피드백 템플릿 응답 DTO")
data class FeedbackTemplateRes(

    val id: Long,
    val name: String,
    val subject: Subject,
    val content: String,
    val usageCount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: FeedbackTemplate) = FeedbackTemplateRes(
            id = entity.id!!,
            name = entity.name,
            subject = entity.subject,
            content = entity.content,
            usageCount = entity.usageCount,
            createdAt = entity.createdAt!!
        )
    }
}