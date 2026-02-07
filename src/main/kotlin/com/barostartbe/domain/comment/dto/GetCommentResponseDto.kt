package com.barostartbe.domain.comment.dto

import com.barostartbe.domain.comment.entity.Comment

data class GetCommentResponseDto (
    val content: String,
    val name: String,
){
    companion object{
        fun from(comment: Comment): GetCommentResponseDto{
            return GetCommentResponseDto(
                content = comment.content!!,
                name = comment.mentee!!.name!!
            )
        }
    }
}