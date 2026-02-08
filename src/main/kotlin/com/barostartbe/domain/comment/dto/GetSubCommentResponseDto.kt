package com.barostartbe.domain.comment.dto

import com.barostartbe.domain.comment.entity.SubComment

data class GetSubCommentResponseDto(
    val subCommentId: Long,
    val name: String,
    val subContent: String,
    val userType: String
){
    companion object{
        fun from(subComment: SubComment): GetSubCommentResponseDto{
            return GetSubCommentResponseDto(
                subCommentId = subComment.id!!,
                name = subComment.user.name!!,
                subContent = subComment.content!!,
                userType = subComment.user.role!!.name
            )
        }
    }
}
