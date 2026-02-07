package com.barostartbe.domain.comment.dto

import io.swagger.v3.oas.annotations.media.Schema

data class UpdateSubCommentRequestDto(
    @Schema(description = "코멘트 답글", example = "정말 수고하셨어요")
    val content: String
)
