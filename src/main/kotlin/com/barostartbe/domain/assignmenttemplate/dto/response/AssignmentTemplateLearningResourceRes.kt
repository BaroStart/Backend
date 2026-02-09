package com.barostartbe.domain.assignmenttemplate.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "과제 템플릿에 연결된 학습 자료 정보")
data class AssignmentTemplateLearningResourceRes(

    @Schema(description = "학습 자료 ID", example = "10")
    val id: Long,

    @Schema(description = "파일명", example = "chapter1.pdf")
    val fileName: String,

    @Schema(description = "파일 다운로드 URL", example = "https://...downloadlink.com/file.pdf")
    val url: String
)
