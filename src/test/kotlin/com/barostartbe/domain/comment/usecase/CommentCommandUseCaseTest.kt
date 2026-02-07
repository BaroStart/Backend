package com.barostartbe.domain.comment.usecase

import com.barostartbe.domain.comment.dto.CreateCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateSubCommentRequestDto
import com.barostartbe.domain.comment.dto.UpdateCommentRequestDto
import com.barostartbe.domain.comment.dto.UpdateSubCommentRequestDto
import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.comment.entity.SubComment
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.comment.repository.SubCommentRepository
import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.user.entity.Role
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class CommentCommandUseCaseTest : DescribeSpec({
    val commentRepository = mockk<CommentRepository>()
    val userRepository = mockk<UserRepository>()
    val subCommentRepository = mockk<SubCommentRepository>()
    val useCase = CommentCommandUseCase(
        commentRepository = commentRepository,
        userRepository = userRepository,
        subCommentRepository = subCommentRepository
    )

    beforeTest {
        clearAllMocks()
    }

    describe("createComment") {
        val request = CreateCommentRequestDto(content = "new comment")

        it("saves a comment when the user is a mentee") {
            val mentee = createMentee()
            val savedComment = mockk<Comment> {
                every { id } returns 10L
            }
            val commentSlot = slot<Comment>()
            every { commentRepository.save(capture(commentSlot)) } returns savedComment

            val response = useCase.createComment(mentee, request)

            response.commentId shouldBe 10L
            commentSlot.captured.mentee shouldBe mentee
            commentSlot.captured.content shouldBe request.content
            verify(exactly = 1) { commentRepository.save(any()) }
            confirmVerified(commentRepository)
        }

        it("throws NO_AUTH when the user is not a mentee") {
            val user = User(
                loginId = "mentor1",
                password = "pw",
                name = "mentor",
                role = Role.MENTOR,
                nickname = "mentor-nick"
            )

            val exception = shouldThrow<ServiceException> {
                useCase.createComment(user, request)
            }

            exception.errorCode shouldBe ErrorCode.NO_AUTH
            verify { commentRepository wasNot Called }
        }
    }

    describe("updateComment") {
        val commentId = 5L
        val request = UpdateCommentRequestDto(content = "updated content")

        it("updates existing comment content") {
            val mentee = createMentee()
            val comment = Comment(mentee, "original content")
            every { commentRepository.findByIdOrNull(commentId) } returns comment
            every { commentRepository.save(comment) } returns comment

            useCase.updateComment(commentId, request)

            comment.content shouldBe request.content
            verify(exactly = 1) {
                commentRepository.findByIdOrNull(commentId)
                commentRepository.save(comment)
            }
            confirmVerified(commentRepository)
        }

        it("throws NOT_FOUND when comment does not exist") {
            every { commentRepository.findByIdOrNull(commentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.updateComment(commentId, request)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { commentRepository.findByIdOrNull(commentId) }
            confirmVerified(commentRepository)
        }
    }

    describe("deleteComment") {
        val commentId = 7L

        it("deletes comment and its sub-comments when found") {
            val mentee = createMentee()
            val comment = Comment(mentee, "to delete")
            every { commentRepository.findByIdOrNull(commentId) } returns comment
            every { subCommentRepository.deleteAllByComment(comment) } just Runs
            every { commentRepository.delete(comment) } just Runs

            useCase.deleteComment(commentId)

            verify(exactly = 1) {
                commentRepository.findByIdOrNull(commentId)
                subCommentRepository.deleteAllByComment(comment)
                commentRepository.delete(comment)
            }
            confirmVerified(commentRepository, subCommentRepository)
        }

        it("throws NOT_FOUND when comment is missing") {
            every { commentRepository.findByIdOrNull(commentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.deleteComment(commentId)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { commentRepository.findByIdOrNull(commentId) }
            verify { subCommentRepository wasNot Called }
            confirmVerified(commentRepository, subCommentRepository)
        }
    }

    describe("createSubComment") {
        val request = CreateSubCommentRequestDto(
            commentId = 12L,
            subContent = "reply"
        )

        it("saves sub-comment when user and comment exist") {
            val user = createMentee()
            val comment = Comment(user, "parent")
            val savedSubComment = mockk<SubComment> {
                every { id } returns 88L
            }
            val subCommentSlot = slot<SubComment>()
            every { commentRepository.findByIdOrNull(request.commentId) } returns comment
            every { subCommentRepository.save(capture(subCommentSlot)) } returns savedSubComment

            val response = useCase.createSubComment(user, request)

            response.subCommentId shouldBe 88L
            subCommentSlot.captured.user shouldBe user
            subCommentSlot.captured.comment shouldBe comment
            subCommentSlot.captured.content shouldBe request.subContent
            verify(exactly = 1) {
                commentRepository.findByIdOrNull(request.commentId)
                subCommentRepository.save(any())
            }
            confirmVerified(commentRepository, subCommentRepository)
        }

        it("throws NOT_FOUND when comment does not exist") {
            val user = createMentee()
            every { commentRepository.findByIdOrNull(request.commentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.createSubComment(user, request)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) {
                commentRepository.findByIdOrNull(request.commentId)
            }
            verify { subCommentRepository wasNot Called }
            confirmVerified(commentRepository, subCommentRepository)
        }
    }

    describe("updateSubComment") {
        val subCommentId = 55L
        val request = UpdateSubCommentRequestDto(content = "edited reply")

        it("updates existing sub-comment content") {
            val user = createMentee()
            val comment = Comment(user, "parent")
            val subComment = SubComment(comment, user, "old reply")
            every { subCommentRepository.findByIdOrNull(subCommentId) } returns subComment
            every { subCommentRepository.save(subComment) } returns subComment

            useCase.updateSubComment(subCommentId, request)

            subComment.content shouldBe request.content
            verify(exactly = 1) {
                subCommentRepository.findByIdOrNull(subCommentId)
                subCommentRepository.save(subComment)
            }
            confirmVerified(subCommentRepository)
        }

        it("throws NOT_FOUND when sub-comment is missing") {
            every { subCommentRepository.findByIdOrNull(subCommentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.updateSubComment(subCommentId, request)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { subCommentRepository.findByIdOrNull(subCommentId) }
            confirmVerified(subCommentRepository)
        }
    }

    describe("deleteSubComment") {
        val subCommentId = 21L

        it("delegates deletion to repository") {
            every { subCommentRepository.deleteById(subCommentId) } just Runs

            useCase.deleteSubComment(subCommentId)

            verify(exactly = 1) { subCommentRepository.deleteById(subCommentId) }
            confirmVerified(subCommentRepository)
        }
    }
}) {
    companion object {
        private fun createMentee(
            loginId: String = "mentee1",
            nickname: String = "mentee-nick"
        ): Mentee {
            return Mentee(
                loginId = loginId,
                password = "password",
                name = "mentee",
                nickname = nickname,
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "CS"
            )
        }
    }
}
