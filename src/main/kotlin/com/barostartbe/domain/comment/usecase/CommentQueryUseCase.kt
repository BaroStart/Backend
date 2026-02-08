package com.barostartbe.domain.comment.usecase

import com.barostartbe.domain.admin.entity.MentorMenteeMapping
import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.comment.dto.GetCommentResponseDto
import com.barostartbe.domain.comment.dto.GetSubCommentResponseDto
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.comment.repository.SubCommentRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@QueryUseCase
class CommentQueryUseCase(
    private val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
    private val commentRepository: CommentRepository,
    private val mentorRepository: MentorRepository,
    private val subCommentRepository: SubCommentRepository
) {
    fun getAllCommentsForMentor(mentorId: Long): List<GetCommentResponseDto> {
        val mentor = mentorRepository.findByIdOrNull(mentorId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)
        val mentee = mentorMenteeMappingRepository.findAllByMentor(mentor)
            .map(MentorMenteeMapping::mentee)
            .toList()
        return commentRepository.findAllByMenteeIn(mentee)
            .map(GetCommentResponseDto::from)
            .toList()
    }

    fun getAllSubComments(commentId: Long): List<GetSubCommentResponseDto> {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw ServiceException(ErrorCode.NOT_FOUND)
        return subCommentRepository.findAllByComment(comment)
            .map(GetSubCommentResponseDto::from)
            .toList()
    }

    fun hasMoreThanTenComments(menteeId: Long): Boolean =
        commentRepository.countByMentee_Id(menteeId) > 10
}
