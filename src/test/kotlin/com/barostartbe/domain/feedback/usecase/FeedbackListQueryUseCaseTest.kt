package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.dto.response.MenteeFeedbackDetailRes
import com.barostartbe.domain.feedback.entity.Feedback
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDateTime
import java.util.Optional

class FeedbackListQueryUseCaseTest : DescribeSpec({

    val assignmentRepository = mockk<AssignmentRepository>()
    val feedbackRepository = mockk<FeedbackRepository>()

    val useCase = FeedbackListQueryUseCase(
        assignmentRepository,
        feedbackRepository
    )

    fun assignmentFixture(
        assignmentId: Long,
        mentorId: Long,
        menteeId: Long,
        status: AssignmentStatus
    ): Assignment {

        val mentor = mockk<Mentor> {
            every { id } returns mentorId
            every { name } returns "멘토"
        }

        val mentee = mockk<Mentee> {
            every { id } returns menteeId
            every { name } returns "멘티"
            every { grade } returns mockk { every { name } returns "중1" }
            every { school } returns mockk { every { name } returns "학교" }
        }

        return mockk {
            every { id } returns assignmentId
            every { this@mockk.mentor } returns mentor
            every { this@mockk.mentee } returns mentee
            every { this@mockk.status } returns status
            every { title } returns "과제 제목"
            every { dueDate } returns LocalDateTime.now().plusDays(1)
            every { submittedAt } returns LocalDateTime.now().minusHours(1)
            every { memo } returns "제출 메모"
            every { subject } returns mockk()
        }
    }

    describe("getListByMentor") {

        it("멘토 기준 제출된 과제가 없으면 빈 리스트를 반환한다") {
            every {
                assignmentRepository.findAllByMentorIdAndStatusInOrderBySubmittedAtDesc(
                    mentorId = 1L,
                    status = any()
                )
            } returns emptyList()

            val result = useCase.getListByMentor(mentorId = 1L)

            result shouldBe emptyList()
        }

        it("멘토 기준 피드백 목록을 반환한다") {

            val assignment = assignmentFixture(
                assignmentId = 1L,
                mentorId = 1L,
                menteeId = 10L,
                status = AssignmentStatus.SUBMITTED
            )

            every {
                assignmentRepository.findAllByMentorIdAndStatusInOrderBySubmittedAtDesc(
                    mentorId = 1L,
                    status = any()
                )
            } returns listOf(assignment)

            every {
                feedbackRepository.findAllByAssignmentIdIn(listOf(1L))
            } returns emptyList()

            val result = useCase.getListByMentor(
                mentorId = 1L,
                now = LocalDateTime.now()
            )

            result.size shouldBe 1
        }
    }

    describe("getDetail (멘티)") {

        it("과제가 없으면 ASSIGNMENT_NOT_FOUND 예외") {
            every { assignmentRepository.findById(1L) } returns Optional.empty()

            val ex = shouldThrow<ServiceException> {
                useCase.getDetail(menteeId = 1L, assignmentId = 1L)
            }

            ex.errorCode shouldBe ErrorCode.ASSIGNMENT_NOT_FOUND
        }

        it("본인 과제가 아니면 NO_AUTH 예외") {
            val assignment = assignmentFixture(
                assignmentId = 1L,
                mentorId = 1L,
                menteeId = 99L,
                status = AssignmentStatus.FEEDBACKED
            )

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)

            val ex = shouldThrow<ServiceException> {
                useCase.getDetail(menteeId = 1L, assignmentId = 1L)
            }

            ex.errorCode shouldBe ErrorCode.NO_AUTH
        }

        it("피드백이 없으면 FEEDBACK_NOT_FOUND 예외") {
            val assignment = assignmentFixture(
                assignmentId = 1L,
                mentorId = 1L,
                menteeId = 1L,
                status = AssignmentStatus.FEEDBACKED
            )

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)
            every { feedbackRepository.findByAssignmentId(1L) } returns null

            val ex = shouldThrow<ServiceException> {
                useCase.getDetail(menteeId = 1L, assignmentId = 1L)
            }

            ex.errorCode shouldBe ErrorCode.FEEDBACK_NOT_FOUND
        }

        it("정상적으로 멘티 피드백 상세를 반환한다") {
            val assignment = assignmentFixture(
                assignmentId = 1L,
                mentorId = 1L,
                menteeId = 1L,
                status = AssignmentStatus.FEEDBACKED
            )

            val feedback = mockk<Feedback> {
                every { summary } returns "요약"
                every { content } returns "피드백 내용"
                every { createdAt } returns LocalDateTime.of(2026, 2, 9, 10, 0)
            }

            every { assignmentRepository.findById(1L) } returns Optional.of(assignment)
            every { feedbackRepository.findByAssignmentId(1L) } returns feedback

            val result = useCase.getDetail(
                menteeId = 1L,
                assignmentId = 1L
            )

            result shouldBe MenteeFeedbackDetailRes(
                mentorName = "멘토",
                assignmentTitle = "과제 제목",
                assignmentDueDate = assignment.dueDate,
                feedbackSummary = "요약",
                feedbackContent = "피드백 내용",
                feedbackTime = LocalDateTime.of(2026, 2, 9, 10, 0)
            )
        }
    }
})
