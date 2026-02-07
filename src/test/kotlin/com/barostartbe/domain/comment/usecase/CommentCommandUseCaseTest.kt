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

    describe("댓글 생성") {
        val request = CreateCommentRequestDto(content = "new comment")

        it("사용자가 멘티라면 댓글을 저장한다") {
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

        it("사용자가 멘티가 아니면 권한 없음 예외를 던진다") {
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

    describe("댓글 수정") {
        val commentId = 5L
        val request = UpdateCommentRequestDto(content = "updated content")

        it("기존 댓글 내용을 수정한다") {
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

        it("댓글이 존재하지 않으면 예외를 던진다") {
            every { commentRepository.findByIdOrNull(commentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.updateComment(commentId, request)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { commentRepository.findByIdOrNull(commentId) }
            confirmVerified(commentRepository)
        }
    }

    describe("댓글 삭제") {
        val commentId = 7L

        it("댓글이 존재하면 댓글과 대댓글을 삭제한다") {
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

        it("댓글이 없으면 예외를 던진다") {
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

    describe("대댓글 생성") {
        val request = CreateSubCommentRequestDto(
            commentId = 12L,
            subContent = "reply"
        )

        it("사용자와 댓글이 존재하면 대댓글을 저장한다") {
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

        it("댓글이 존재하지 않으면 예외를 던진다") {
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

    describe("대댓글 수정") {
        val subCommentId = 55L
        val request = UpdateSubCommentRequestDto(content = "edited reply")

        it("기존 대댓글 내용을 수정한다") {
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

        it("대댓글이 없으면 예외를 던진다") {
            every { subCommentRepository.findByIdOrNull(subCommentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.updateSubComment(subCommentId, request)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { subCommentRepository.findByIdOrNull(subCommentId) }
            confirmVerified(subCommentRepository)
        }
    }

    describe("대댓글 삭제") {
        val subCommentId = 21L

        it("대댓글을 삭제한다") {
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