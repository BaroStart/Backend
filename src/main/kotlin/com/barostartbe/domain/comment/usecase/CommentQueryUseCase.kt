package com.barostartbe.domain.comment.usecase

import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@QueryUseCase
class CommentQueryUseCase(
    private val commentRepository: CommentRepository
) {
    fun getComment(commentId: Long): Comment {
        return commentRepository.findByIdOrNull(commentId) ?: throw ServiceException(ErrorCode.NOT_FOUND)
    }

}