package com.barostartbe.domain.comment.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.comment.dto.CreateCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateCommentResponseDto
import com.barostartbe.domain.comment.dto.CreateSubCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateSubCommentResponseDto
import com.barostartbe.domain.comment.dto.UpdateCommentRequestDto
import com.barostartbe.domain.comment.dto.UpdateSubCommentRequestDto
import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.comment.entity.SubComment
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.comment.repository.SubCommentRepository
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.notification.usecase.SendNotificationUseCase
import com.barostartbe.domain.user.entity.Role
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class CommentCommandUseCase(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val subCommentRepository: SubCommentRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val mentorMenteeMappingRepository: MentorMenteeMappingRepository
) {
    fun createComment(user: User, request: CreateCommentRequestDto): CreateCommentResponseDto {

        if (user.role != Role.MENTEE) throw ServiceException(ErrorCode.NO_AUTH)

        val savedComment = commentRepository.save(Comment.of(user as Mentee, request.content))

        mentorMenteeMappingRepository.findAllByMentee(user).forEach { mapping ->
            sendNotificationUseCase.execute(
                SendNotificationRequest(
                    receiverId = mapping.mentor.id!!,
                    title = Type.NEW_COMMENT.titleFormat,
                    message = Type.NEW_COMMENT.messageFormat.format(user.name),
                )
            )
        }

        return CreateCommentResponseDto(savedComment.id!!)
    }

    fun updateComment(commentId: Long, request: UpdateCommentRequestDto) {

        val comment = commentRepository.findByIdOrNull(commentId) ?: throw ServiceException(ErrorCode.NOT_FOUND)

        comment.content = request.content

        commentRepository.save(comment)
    }

    fun deleteComment(commentId: Long) {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw ServiceException(ErrorCode.NOT_FOUND)
        subCommentRepository.deleteAllByComment(comment)
        commentRepository.delete(comment)
    }

    fun createSubComment(user: User, request: CreateSubCommentRequestDto): CreateSubCommentResponseDto {

        val comment = commentRepository.findByIdOrNull(request.commentId) ?: throw ServiceException(
            ErrorCode.NOT_FOUND
        )

        val savedSubComment = subCommentRepository.save(SubComment.of(user, comment, request.subContent))

        if (user.role == Role.MENTEE) {
            mentorMenteeMappingRepository.findAllByMentee(user as Mentee).forEach { mapping ->
                sendNotificationUseCase.execute(
                    SendNotificationRequest(
                        receiverId = mapping.mentor.id!!,
                        title = Type.NEW_COMMENT.titleFormat,
                        message = Type.NEW_COMMENT.messageFormat.format(user.name),
                    )
                )
            }
        }

        if (user.role == Role.MENTOR) {
            sendNotificationUseCase.execute(
                SendNotificationRequest(
                    receiverId = comment.mentee!!.id!!,
                    title = Type.NEW_COMMENT.titleFormat,
                    message = Type.NEW_COMMENT.messageFormat.format(user.name),
                )
            )
        }

        return CreateSubCommentResponseDto(savedSubComment.id!!)
    }

    fun updateSubComment(subCommentId: Long, request: UpdateSubCommentRequestDto) {

        val subComment =
            subCommentRepository.findByIdOrNull(subCommentId) ?: throw ServiceException(ErrorCode.NOT_FOUND)

        subComment.content = request.content

        subCommentRepository.save(subComment)
    }

    fun deleteSubComment(subCommendId: Long) = subCommentRepository.deleteById(subCommendId)
}
