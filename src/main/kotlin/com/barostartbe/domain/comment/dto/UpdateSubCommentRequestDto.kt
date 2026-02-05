package com.barostartbe.domain.comment.dto

data class UpdateSubCommentRequestDto(
    val subCommentId: Long,
    val content: String
)
