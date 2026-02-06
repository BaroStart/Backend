package com.barostartbe.domain.file.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 파일 업로드 요청 DTO
 */
@Schema(description = "파일 업로드 요청 DTO")
data class FileUploadReq(

    @Schema(description = "파일명", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "fileName은 필수입니다")
    @field:Size(max = 255, message = "fileName은 255자를 초과할 수 없습니다")
    val fileName: String,

    @Schema(
        description = "Object Storage 내부 파일 경로 (objectKey)",
        example = "assignments/2026/02/06/uuid_sample.pdf",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank
    val filePath: String
)