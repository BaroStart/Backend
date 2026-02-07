package com.barostartbe.domain.comment.usecase

import com.barostartbe.domain.admin.entity.MentorMenteeMapping
import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.comment.dto.GetCommentResponseDto
import com.barostartbe.domain.comment.dto.GetSubCommentResponseDto
import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.comment.entity.SubComment
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.comment.repository.SubCommentRepository
import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class CommentQueryUseCaseTest : DescribeSpec({
    val mentorMenteeMappingRepository = mockk<MentorMenteeMappingRepository>()
    val commentRepository = mockk<CommentRepository>()
    val mentorRepository = mockk<MentorRepository>()
    val subCommentRepository = mockk<SubCommentRepository>()
    val useCase = CommentQueryUseCase(
        mentorMenteeMappingRepository = mentorMenteeMappingRepository,
        commentRepository = commentRepository,
        mentorRepository = mentorRepository,
        subCommentRepository = subCommentRepository
    )

    beforeTest { clearAllMocks() }

    describe("멘토의 모든 댓글 조회") {
        val mentorId = 11L

        it("멘토와 매핑된 멘티들의 모든 댓글을 반환한다") {
            val mentor = createMentor()
            val menteeA = createMentee(loginId = "menteeA", nickname = "mentee-a", name = "A")
            val menteeB = createMentee(loginId = "menteeB", nickname = "mentee-b", name = "B")
            val mappingA = MentorMenteeMapping(mentor = mentor, mentee = menteeA)
            val mappingB = MentorMenteeMapping(mentor = mentor, mentee = menteeB)
            val commentA = Comment(mentee = menteeA, content = "comment-a")
            val commentB = Comment(mentee = menteeB, content = "comment-b")

            every { mentorRepository.findByIdOrNull(mentorId) } returns mentor
            every { mentorMenteeMappingRepository.findAllByMentor(mentor) } returns listOf(mappingA, mappingB)
            every { commentRepository.findAllByMenteeIn(listOf(menteeA, menteeB)) } returns listOf(commentA, commentB)

            val result = useCase.getAllCommentsForMentor(mentorId)

            result shouldContainExactly listOf(
                GetCommentResponseDto.from(commentA),
                GetCommentResponseDto.from(commentB)
            )
            verify(exactly = 1) {
                mentorRepository.findByIdOrNull(mentorId)
                mentorMenteeMappingRepository.findAllByMentor(mentor)
                commentRepository.findAllByMenteeIn(listOf(menteeA, menteeB))
            }
            confirmVerified(mentorRepository, mentorMenteeMappingRepository, commentRepository)
        }

        it("멘토가 없으면 예외를 던진다") {
            every { mentorRepository.findByIdOrNull(mentorId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.getAllCommentsForMentor(mentorId)
            }

            exception.errorCode shouldBe ErrorCode.USER_NOT_FOUND
            verify(exactly = 1) { mentorRepository.findByIdOrNull(mentorId) }
            verify { mentorMenteeMappingRepository wasNot Called }
            verify { commentRepository wasNot Called }
            confirmVerified(mentorRepository, mentorMenteeMappingRepository, commentRepository)
        }
    }

    describe("모든 대댓글 조회") {
        val commentId = 22L

        it("주어진 댓글의 대댓글들을 반환한다") {
            val mentee = createMentee()
            val comment = Comment(mentee = mentee, content = "parent")
            val mentorResponder = createMentor(loginId = "mentor2", nickname = "mentor2", name = "Mentor2")
            val menteeResponder = mentee
            val subCommentA = SubComment(comment = comment, user = mentorResponder, content = "reply-mentor")
            val subCommentB = SubComment(comment = comment, user = menteeResponder, content = "reply-mentee")

            every { commentRepository.findByIdOrNull(commentId) } returns comment
            every { subCommentRepository.findAllByComment(comment) } returns listOf(subCommentA, subCommentB)

            val result = useCase.getAllSubComments(commentId)

            result shouldContainExactly listOf(
                GetSubCommentResponseDto.from(subCommentA),
                GetSubCommentResponseDto.from(subCommentB)
            )
            verify(exactly = 1) {
                commentRepository.findByIdOrNull(commentId)
                subCommentRepository.findAllByComment(comment)
            }
            confirmVerified(commentRepository, subCommentRepository)
        }

        it("댓글이 존재하지 않으면 예외를 던진다") {
            every { commentRepository.findByIdOrNull(commentId) } returns null

            val exception = shouldThrow<ServiceException> {
                useCase.getAllSubComments(commentId)
            }

            exception.errorCode shouldBe ErrorCode.NOT_FOUND
            verify(exactly = 1) { commentRepository.findByIdOrNull(commentId) }
            verify { subCommentRepository wasNot Called }
            confirmVerified(commentRepository, subCommentRepository)
        }
    }

    describe("댓글 10개 초과 확인") {
        val menteeId = 1L

        it("댓글 수가 10개를 초과하면 true를 반환한다") {
            every { commentRepository.countByMentee_Id(menteeId) } returns 11
            useCase.hasMoreThanTenComments(menteeId) shouldBe true
        }

        it("댓글 수가 10개 이하이면 false를 반환한다") {
            every { commentRepository.countByMentee_Id(menteeId) } returns 10
            useCase.hasMoreThanTenComments(menteeId) shouldBe false
        }
    }
}) {
    companion object {
        private fun createMentee(
            loginId: String = "mentee-login",
            nickname: String = "mentee-nick",
            name: String = "Mentee"
        ): Mentee {
            return Mentee(
                loginId = loginId,
                password = "password",
                name = name,
                nickname = nickname,
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "CS"
            )
        }

        private fun createMentor(
            loginId: String = "mentor-login",
            nickname: String = "mentor-nick",
            name: String = "Mentor"
        ): Mentor {
            return Mentor(
                loginId = loginId,
                password = "password",
                name = name,
                nickname = nickname,
                university = "Uni"
            )
        }
    }
}
