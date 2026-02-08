package com.barostartbe.domain.assignmenttemplate.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "과제 템플릿 학습자료 목록 조회 응답")
data class AssignmentTemplateFileListRes(

    @Schema(description = "과목", example = "MATH")
    val subject: Subject,

    @Schema(description = "파일명", example = "chapter1.pdf")
    val fileName: String,

    @Schema(description = "파일 다운로드 URL")
    val url: String
)
