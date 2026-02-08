package com.barostartbe.domain.comment.dto

import com.barostartbe.domain.comment.entity.Comment
import java.time.LocalDateTime

data class GetCommentResponseDto (
    val content: String,
    val name: String,
    val createdAt: LocalDateTime,
){
    companion object{
        fun from(comment: Comment): GetCommentResponseDto{
            return GetCommentResponseDto(
                content = comment.content!!,
                name = comment.mentee!!.name!!,
                createdAt = comment.createdAt!!,
            )
        }
    }
}
