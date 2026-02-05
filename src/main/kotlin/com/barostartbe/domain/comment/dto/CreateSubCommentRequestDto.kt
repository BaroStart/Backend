package com.barostartbe.domain.comment.dto

data class CreateSubCommentRequestDto(
    val commentId: Long,
    val userId: Long,
    val subContent: String
)
