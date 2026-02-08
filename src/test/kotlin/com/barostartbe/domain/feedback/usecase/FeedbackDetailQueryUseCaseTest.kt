package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDateTime
import java.util.Optional

class FeedbackDetailQueryUseCaseTest : DescribeSpec({

    val assignmentRepository = mockk<AssignmentRepository>()
    val feedbackRepository = mockk<FeedbackRepository>()
    val assignmentFileRepository = mockk<AssignmentFileRepository>()

    val useCase = FeedbackDetailQueryUseCase(
        assignmentRepository,
        feedbackRepository,
        assignmentFileRepository
    )

    val fixedNow = LocalDateTime.of(2026, 2, 9, 10, 0)

    fun assignmentFixture(
        mentorId: Long,
        submittedAt: LocalDateTime
    ): Assignment {
        val mentor = mockk<Mentor> {
            every { id } returns mentorId
        }

        val mentee = mockk<Mentee> {
            every { name } returns "멘티"
            every { grade } returns mockk<Grade> { every { name } returns "고3" }
            every { school } returns mockk<School> { every { name } returns "이과" }
        }

        return mockk {
            every { id } returns 1L
            every { this@mockk.mentor } returns mentor
            every { this@mockk.mentee } returns mentee
            every { title } returns "과제 제목"
            every { subject } returns Subject.MATH
            every { memo } returns "제출 메모"
            every { this@mockk.submittedAt } returns submittedAt
        }
    }

    describe("FeedbackDetailQueryUseCase.getDetail") {

        it("과제가 없으면 ASSIGNMENT_NOT_FOUND 예외") {
            every { assignmentRepository.findById(1L) } returns Optional.empty()

            val ex = shouldThrow<ServiceException> {
                useCase.getDetail(mentorId = 1L, assignmentId = 1L, now = fixedNow)
            }

            ex.errorCode shouldBe ErrorCode.ASSIGNMENT_NOT_FOUND
        }

        it("멘토 본인 과제가 아니면 NO_AUTH 예외") {
            val assignment = assignmentFixture(
                mentorId = 99L,
                submittedAt = fixedNow.minusDays(1)
            )

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)

            val ex = shouldThrow<ServiceException> {
                useCase.getDetail(mentorId = 1L, assignmentId = 1L, now = fixedNow)
            }

            ex.errorCode shouldBe ErrorCode.NO_AUTH
        }

        it("피드백이 존재하면 COMPLETED 상태로 반환된다") {
            val assignment = assignmentFixture(
                mentorId = 1L,
                submittedAt = fixedNow.minusDays(1)
            )

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)
            every { feedbackRepository.existsByAssignmentId(1L) } returns true
            every {
                assignmentFileRepository.findAllByAssignmentAndFileType(
                    assignment,
                    AssignmentFileType.SUBMISSION
                )
            } returns listOf(
                mockk<AssignmentFile> {
                    every { url } returns "image-url"
                }
            )

            val result = useCase.getDetail(
                mentorId = 1L,
                assignmentId = 1L,
                now = fixedNow
            )

            result.feedbackStatus shouldBe FeedbackStatus.COMPLETED
            result.submissionImageUrls shouldBe listOf("image-url")
        }

        it("피드백 없고 마감 전이면 WAITING 상태") {
            val assignment = assignmentFixture(
                mentorId = 1L,
                submittedAt = fixedNow.minusHours(1)
            )

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)
            every { feedbackRepository.existsByAssignmentId(1L) } returns false
            every {
                assignmentFileRepository.findAllByAssignmentAndFileType(any(), any())
            } returns emptyList()

            val result = useCase.getDetail(
                mentorId = 1L,
                assignmentId = 1L,
                now = fixedNow
            )

            result.feedbackStatus shouldBe FeedbackStatus.WAITING
        }
    }
})
