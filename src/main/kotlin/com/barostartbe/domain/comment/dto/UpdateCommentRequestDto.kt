package com.barostartbe.domain.comment.dto

data class UpdateCommentRequestDto(
    val commentId: Long,
    val content: String
)
