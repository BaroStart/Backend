package com.barostartbe.domain.assignment.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "과제 첨부 파일(학습자료/제출물) 생성 요청 DTO")
data class AssignmentFileCreateReq(

    @Schema(description = "파일 ID (사전 업로드된 파일)", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "fileId는 필수입니다")
    val fileId: Long,
)
