package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackRes
import com.barostartbe.domain.feedback.entity.Feedback
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentor.entity.Mentor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalDateTime

class DailyFeedbackQueryUseCaseTest : DescribeSpec({

    val feedbackRepository = mockk<FeedbackRepository>()
    val useCase = DailyFeedbackQueryUseCase(feedbackRepository)

    val mentor = mockk<Mentor> {
        every { name } returns "멘토A"
    }

    val mentee = mockk<Mentee>()

    val assignment = mockk<Assignment> {
        every { id } returns 1L
        every { subject } returns Subject.MATH
        every { this@mockk.mentor } returns mentor
        every { this@mockk.mentee } returns mentee
    }

    val feedback = mockk<Feedback> {
        every { this@mockk.assignment } returns assignment
        every { content } returns "피드백 내용"
        every { createdAt } returns LocalDateTime.of(2026, 2, 9, 10, 0)
    }

    val fixedToday = LocalDate.of(2026, 2, 9)

    describe("DailyFeedbackQueryUseCase.getDailyFeedbacks") {

        it("subject가 null이면 전체 피드백을 조회한다") {
            every {
                feedbackRepository.findDailyFeedbacksByMentee(
                    menteeId = 1L,
                    start = any(),
                    end = any()
                )
            } returns listOf(feedback)

            val result: List<DailyFeedbackRes> =
                useCase.getDailyFeedbacks(
                    menteeId = 1L,
                    subject = null,
                    today = fixedToday
                )

            result.size shouldBe 1
            result[0].mentorName shouldBe "멘토A"
            result[0].subject shouldBe Subject.MATH
            result[0].content shouldBe "피드백 내용"
        }

        it("subject가 있으면 과목별 피드백을 조회한다") {
            every {
                feedbackRepository.findDailyFeedbacksByMenteeAndSubject(
                    menteeId = 1L,
                    subject = Subject.MATH,
                    start = any(),
                    end = any()
                )
            } returns listOf(feedback)

            val result =
                useCase.getDailyFeedbacks(
                    menteeId = 1L,
                    subject = Subject.MATH,
                    today = fixedToday
                )

            result.size shouldBe 1
            result[0].subject shouldBe Subject.MATH
        }
    }
})
