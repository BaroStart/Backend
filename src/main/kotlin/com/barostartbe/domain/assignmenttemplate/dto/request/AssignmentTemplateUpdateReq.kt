package com.barostartbe.domain.assignmenttemplate.dto.request

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "과제 템플릿 수정 요청 DTO")
data class AssignmentTemplateUpdateReq(

    @field:NotNull
    @Schema(description = "과목", example = "MATH", requiredMode = Schema.RequiredMode.REQUIRED)
    val subject: Subject,

    @field:NotBlank
    @field:Size(max = 100)
    @Schema(description = "템플릿 이름", example = "수학 기본 문제", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    // 템플릿 설명
    @field:NotBlank
    @Schema(description = "템플릿 설명", example = "수학의 정석 기본 예제 풀기", requiredMode = Schema.RequiredMode.REQUIRED)
    val description: String,

    // 과제 제목
    @field:NotBlank
    @field:Size(max = 100)
    @Schema(description = "과제 제목", example = "문제 1번", requiredMode = Schema.RequiredMode.REQUIRED)
    val title: String,

    // 과제 내용
    @field:NotBlank
    @Schema(description = "과제 내용", example = "문제 1번", requiredMode = Schema.RequiredMode.REQUIRED)
    val content: String,

    val files: List<AssignmentTemplateFileReq>? = null
)

data class AssignmentTemplateFileReq(
    @field:NotBlank
    val fileName: String,

    @field:NotBlank
    val url: String
)
