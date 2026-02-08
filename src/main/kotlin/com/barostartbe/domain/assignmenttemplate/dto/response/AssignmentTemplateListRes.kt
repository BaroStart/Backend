package com.barostartbe.domain.assignmenttemplate.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "과제 템플릿 목록 조회 응답")
data class AssignmentTemplateListRes(

    @Schema(description = "과제 템플릿 ID", example = "1")
    val id: Long,

    @Schema(description = "과목", example = "MATH")
    val subject: Subject,

    @Schema(description = "템플릿 이름", example = "중간고사 대비 템플릿")
    val name: String,

    @Schema(description = "템플릿 설명 (목록에 노출)", example = "중간고사 대비를 위한 기본 학습 템플릿")
    val description: String,

    // 파일 정보 목록 (파일명)
    @Schema(description = "템플릿에 첨부된 학습 자료 파일 목록")
    val fileNames: List<String>
)
