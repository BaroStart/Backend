package com.barostartbe.domain.learningresource.dto.request

import com.barostartbe.domain.assignment.entity.enums.Subject
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Schema(description = "학습 자료 등록 요청 DTO")
data class LearningResourceCreateReq(

    @Schema(description = "과목명", example = "MATH", required = true)
    @field:NotNull
    val subject: Subject,

    @Schema(description = "파일명", example = "수리논술_1.pdf", required = true)
    @field:NotBlank
    val fileName: String,

    @Schema(description = "파일 URL", example = "https://objectstorage.../수리논술_1.pdf", required = true)
    @field:NotBlank
    val fileUrl: String,

    @Schema(description = "파일 크기 (byte 단위)", example = "2048576", required = true)
    @field:Positive
    val fileSize: Long
)
