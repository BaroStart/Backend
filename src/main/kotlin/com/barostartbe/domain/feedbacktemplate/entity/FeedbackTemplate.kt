package com.barostartbe.domain.feedbacktemplate.entity

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.global.common.entity.BaseEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "feedback_templates")
class FeedbackTemplate(

    @Schema(description = "피드백 템플릿 이름")
    @Column(nullable = false, length = 100)
    var name: String,

    @Schema(description = "과목")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var subject: Subject,

    @Schema(description = "피드백 템플릿 본문 전체 내용")
    @Lob
    @Column(nullable = false)
    var content: String,

    @Schema(description = "템플릿 사용 횟수")
    @Column(nullable = false)
    var usageCount: Int = 0

) : BaseEntity() {

    // 사용 횟수 증가
    fun increaseUsageCount() {
        this.usageCount += 1
    }
}
