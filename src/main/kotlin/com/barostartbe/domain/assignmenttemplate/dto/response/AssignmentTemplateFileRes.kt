package com.barostartbe.domain.assignmenttemplate.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "과제 템플릿 첨부 파일 정보")
data class AssignmentTemplateFileRes(

    @Schema(description = "파일명", example = "chapter1.pdf")
    val fileName: String,

    @Schema(description = "파일 접근 URL", example = "https://cdn.barostart.com/templates/chapter1.pdf")
    val url: String
)
