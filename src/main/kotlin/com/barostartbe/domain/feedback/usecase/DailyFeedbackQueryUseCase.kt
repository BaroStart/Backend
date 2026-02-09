package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackRes
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.global.annotation.QueryUseCase
import java.time.LocalDate

@Deprecated("미사용")
@QueryUseCase
class DailyFeedbackQueryUseCase(
    private val feedbackRepository: FeedbackRepository
) {

    // [멘티] 오늘의 피드백 조회
    fun getDailyFeedbacks(
        menteeId: Long,
        subject: Subject?,
        today: LocalDate = LocalDate.now()
    ): List<DailyFeedbackRes> {

        val start = today.atStartOfDay()
        val end = today.plusDays(1).atStartOfDay().minusNanos(1)

        val feedbacks =
            if (subject == null) {
                feedbackRepository.findDailyFeedbacksByMentee(
                    menteeId = menteeId,
                    start = start,
                    end = end
                )
            } else {
                feedbackRepository.findDailyFeedbacksByMenteeAndSubject(
                    menteeId = menteeId,
                    subject = subject,
                    start = start,
                    end = end
                )
            }

        return feedbacks.map { feedback ->
            val assignment = feedback.assignment
            val mentor = assignment.mentor

            DailyFeedbackRes(
                subject = assignment.subject,
                mentorName = mentor.name ?: "멘토",
                feedbackTime = feedback.createdAt!!,
                content = feedback.content,
                assignmentId = assignment.id!!
            )
        }


    }
}
