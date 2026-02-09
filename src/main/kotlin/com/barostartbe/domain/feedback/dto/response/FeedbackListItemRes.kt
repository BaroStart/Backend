package com.barostartbe.domain.feedback.dto.response

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.time.LocalTime

@Schema(description = "피드백 목록 조회 아이템")
data class FeedbackListItemRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "피드백 상태 (DEADLINE / WAITING / COMPLETED)")
    val status: FeedbackStatus,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "과제명")
    val assignmentTitle: String,

    @Schema(description = "멘티 이름")
    val menteeName: String,

    @Schema(description = "과제 제출일시")
    val submittedAt: LocalDateTime
) {

    companion object {

        // AssignmentStatus, 시간 기준으로 화면 상태 계산
        fun from(
            assignment: Assignment,
            menteeName: String,
            now: LocalDateTime
        ): FeedbackListItemRes {

            val deadlineAt = assignment.submittedAt!!
                .toLocalDate()
                .plusDays(1)
                .atTime(LocalTime.of(11, 0)) // 제출 다음날 11시 마감

            val displayStatus = when {
                assignment.status == AssignmentStatus.FEEDBACKED ->
                    FeedbackStatus.COMPLETED


                now.isAfter(deadlineAt) ->
                    FeedbackStatus.DEADLINE

                else ->
                    FeedbackStatus.WAITING
            }

            return FeedbackListItemRes(
                assignmentId = assignment.id!!,
                status = displayStatus,
                subject = assignment.subject,
                assignmentTitle = assignment.title,
                menteeName = menteeName,
                submittedAt = assignment.submittedAt!!
            )
        }
    }
}