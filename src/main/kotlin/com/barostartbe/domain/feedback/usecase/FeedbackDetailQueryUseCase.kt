package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentFileType
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.dto.response.MentorFeedbackDetailRes
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDateTime
import java.time.LocalTime

@QueryUseCase
class FeedbackDetailQueryUseCase(

    private val assignmentRepository: AssignmentRepository,
    private val feedbackRepository: FeedbackRepository,
    private val assignmentFileRepository: AssignmentFileRepository
) {

    // [멘토] 피드백 상세 조회
    fun getDetail(
        mentorId: Long,
        assignmentId: Long,
        now: LocalDateTime = LocalDateTime.now()
    ): MentorFeedbackDetailRes {

        // 과제 조회
        val assignment: Assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_NOT_FOUND) }

        // 권한 체크 (멘토 본인 과제인지)
        if (assignment.mentor.id != mentorId) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 피드백 존재 여부
        val hasFeedback = feedbackRepository.existsByAssignmentId(assignmentId)

        // 마감 기준 계산 (제출 다음날 오전 11시)
        val deadlineAt = assignment.submittedAt!!
            .toLocalDate()
            .plusDays(1)
            .atTime(LocalTime.of(11, 0))

        val feedbackStatus = when {
            hasFeedback -> FeedbackStatus.COMPLETED
            now.isAfter(deadlineAt) -> FeedbackStatus.DEADLINE
            else -> FeedbackStatus.WAITING
        }

        // 제출 이미지 URL 조회
        val submissionImageUrls: List<String> =
            assignmentFileRepository
                .findAllByAssignmentAndFileType(
                    assignment = assignment,
                    fileType = AssignmentFileType.SUBMISSION
                )
                .map { it.url!! }


        val mentee = assignment.mentee

        return MentorFeedbackDetailRes(
            assignmentId = assignment.id!!,
            feedbackStatus = feedbackStatus,

            menteeName = mentee.name ?: "알 수 없음",
            menteeGrade = mentee.grade!!.name,
            menteeTrack = mentee.school!!.name,

            assignmentTitle = assignment.title,
            subject = assignment.subject,
            submittedAt = assignment.submittedAt!!,

            submissionMemo = assignment.memo,
            submissionImageUrls = submissionImageUrls
        )
    }
}
