package com.barostartbe.domain.comment.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CreateSubCommentRequestDto(
    @Schema(description = "코멘트 id", example = "1")
    val commentId: Long,

    @Schema(description = "코멘트 답글", example = "수고하셨습니다.")
    val subContent: String
)
