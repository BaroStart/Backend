package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.feedback.dto.response.DailyFeedbackSummaryRes
import com.barostartbe.domain.feedback.entity.Feedback
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.mentor.entity.Mentor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDateTime

class DailyFeedbackSummaryQueryUseCaseTest : DescribeSpec({

    val feedbackRepository = mockk<FeedbackRepository>()
    val useCase = DailyFeedbackSummaryQueryUseCase(feedbackRepository)

    describe("DailyFeedbackSummaryQueryUseCase.getDailySummaries") {

        it("멘티의 오늘 피드백 요약 목록을 반환한다") {
            // given
            val mentor = mockk<Mentor> {
                every { name } returns "홍길동"
            }

            val assignment = mockk<Assignment> {
                every { this@mockk.mentor } returns mentor
            }

            val feedback = mockk<Feedback> {
                every { this@mockk.assignment } returns assignment
                every { summary } returns "요약 내용"
                every { createdAt } returns LocalDateTime.of(2026, 2, 9, 10, 0)
            }

            every {
                feedbackRepository.findDailyFeedbackSummaries(
                    menteeId = 1L,
                    start = any(),
                    end = any()
                )
            } returns listOf(feedback)

            // when
            val result = useCase.getDailySummaries(menteeId = 1L)

            // then
            result shouldBe listOf(
                DailyFeedbackSummaryRes(
                    mentorName = "홍길동",
                    summary = "요약 내용",
                    createdAt = LocalDateTime.of(2026, 2, 9, 10, 0)
                )
            )

            verify(exactly = 1) {
                feedbackRepository.findDailyFeedbackSummaries(
                    menteeId = 1L,
                    start = any(),
                    end = any()
                )
            }
        }

        it("오늘 피드백이 없으면 빈 리스트를 반환한다") {
            // given
            every {
                feedbackRepository.findDailyFeedbackSummaries(
                    menteeId = 1L,
                    start = any(),
                    end = any()
                )
            } returns emptyList()

            // when
            val result = useCase.getDailySummaries(menteeId = 1L)

            // then
            result shouldBe emptyList()
        }
    }
})
