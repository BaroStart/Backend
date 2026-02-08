package com.barostartbe.domain.assignmenttemplate.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject

// 과제 템플릿 목표별 조회 응답 DTO (과제 생성시 사용)
data class AssignmentTemplateByGoalRes(
    val goalName: String,
    val subject: Subject,
    val templates: List<TemplateRes>
) {

    data class TemplateRes(
        val templateId: Long,
        val name: String,
        val description: String?,
        val title: String,
        val content: String,
        val files: List<TemplateFileRes>
    )

    data class TemplateFileRes(
        val id: Long,
        val fileName: String,
        val url: String
    )
}
