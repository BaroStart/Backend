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
import com.barostartbe.domain.user.entity.Role
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

    describe("getAllCommentsForMentor") {
        val mentorId = 11L

        it("returns all comments for mentees mapped to the mentor") {
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

        it("throws USER_NOT_FOUND when mentor is absent") {
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

    describe("getAllSubComments") {
        val commentId = 22L

        it("returns sub-comments for the given comment") {
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

        it("throws NOT_FOUND when the comment does not exist") {
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
