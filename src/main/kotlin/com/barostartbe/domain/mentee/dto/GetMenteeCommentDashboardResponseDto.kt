package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.comment.entity.Comment
import java.time.LocalDateTime

data class GetMenteeCommentDashboardResponseDto(
    val commentId: Long,
    val menteeName: String,
    val content: String,
    val createdAt: LocalDateTime
){
    companion object {
        fun from(comment: Comment) : GetMenteeCommentDashboardResponseDto{
            return GetMenteeCommentDashboardResponseDto(
                commentId = comment.id!!,
                menteeName = comment.mentee!!.name!!,
                content = comment.content!!,
                createdAt = comment.createdAt!!
            )
        }
    }
}
