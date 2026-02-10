package com.barostartbe.domain.comment.dto

import com.barostartbe.domain.comment.entity.Comment
import java.time.LocalDateTime

data class GetCommentResponseDto (
    val id: Long,
    val content: String,
    val name: String,
    val createdAt: LocalDateTime,
){
    companion object{
        fun from(comment: Comment): GetCommentResponseDto{
            return GetCommentResponseDto(
                id = comment.id!!,
                content = comment.content!!,
                name = comment.mentee!!.name!!,
                createdAt = comment.createdAt!!,
            )
        }
    }
}
