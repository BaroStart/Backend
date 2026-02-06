package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "[멘토] 학습자료 목록 조회 dto")
data class AssignmentMaterialRes(

    @Schema(description = "파일 ID")
    val assignmentFileId: Long,

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "과제 제목")
    val assignmentTitle: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "파일명")
    val fileName: String,

    @Schema(description = "파일 경로")
    val filePath: String,
)