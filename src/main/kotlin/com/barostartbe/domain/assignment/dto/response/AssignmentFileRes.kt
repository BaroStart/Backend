package com.barostartbe.domain.assignment.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "과제 파일 응답 DTO")
data class AssignmentFileRes(

    @Schema(description = "과제-파일 매핑 ID")
    val assignmentFileId: Long,

    @Schema(description = "파일 ID")
    val fileId: Long,

    @Schema(description = "파일 사용 용도 (MATERIAL / SUBMISSION)")
    val usage: String,

    @Schema(description = "다운로드 URL (Pre-Authenticated URL)")
    val downloadUrl: String?
)