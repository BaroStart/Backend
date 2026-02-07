package com.barostartbe.domain.comment.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class CreateCommentRequestDto(
    @Schema(description = "코멘트 내용", example = "안녕하세요")
    @NotBlank
    val content: String
)
