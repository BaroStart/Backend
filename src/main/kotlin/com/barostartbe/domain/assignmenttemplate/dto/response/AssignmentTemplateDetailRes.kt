package com.barostartbe.domain.assignmenttemplate.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "과제 템플릿 상세 조회 응답 DTO")
data class AssignmentTemplateDetailRes(

    @Schema(description = "템플릿 식별자", example = "1")
    val id: Long,

    @Schema(description = "과목", example = "MATH")
    val subject: Subject,

    @Schema(description = "템플릿 이름", example = "수학 기본 문제")
    val name: String,

    @Schema(description = "템플릿 설명", example = "기본 예제 문제 풀기")
    val description: String,

    @Schema(description = "과제 제목", example = "문제 1번")
    val title: String,

    @Schema(description = "과제 내용", example = "다음 문제를 풀어보세요")
    val content: String,

    @Schema(description = "템플릿에 첨부된 학습 자료 파일 목록")
    val files: List<AssignmentTemplateFileRes>
)
