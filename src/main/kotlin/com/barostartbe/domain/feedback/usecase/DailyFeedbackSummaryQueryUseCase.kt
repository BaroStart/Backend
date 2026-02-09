package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.feedback.dto.response.DailyFeedbackSummaryRes
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.global.annotation.QueryUseCase
import java.time.LocalDate

@Deprecated("미사용")
@QueryUseCase
class DailyFeedbackSummaryQueryUseCase(
    private val feedbackRepository: FeedbackRepository
) {

    // [멘티] 오늘의 피드백 요약 조회
    fun getDailySummaries(menteeId: Long): List<DailyFeedbackSummaryRes> {

        val today = LocalDate.now()
        val start = today.atStartOfDay()
        val end = today.plusDays(1).atStartOfDay().minusNanos(1)

        return feedbackRepository
            .findDailyFeedbackSummaries(menteeId, start, end)
            .map {
                DailyFeedbackSummaryRes(
                    mentorName = it.assignment.mentor.name ?: "알 수 없음",
                    summary = it.summary,
                    createdAt = it.createdAt!!
                )
            }
    }
}
