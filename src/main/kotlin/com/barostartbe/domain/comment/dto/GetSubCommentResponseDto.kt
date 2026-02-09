package com.barostartbe.domain.comment.dto

import com.barostartbe.domain.comment.entity.SubComment
import java.time.LocalDateTime

data class GetSubCommentResponseDto(
    val subCommentId: Long,
    val name: String,
    val subContent: String,
    val userType: String,
    val createdAt: LocalDateTime
){
    companion object{
        fun from(subComment: SubComment): GetSubCommentResponseDto{
            return GetSubCommentResponseDto(
                subCommentId = subComment.id!!,
                name = subComment.user.name!!,
                subContent = subComment.content!!,
                userType = subComment.user.role!!.name,
                createdAt = subComment.createdAt!!,
            )
        }
    }
}
