package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.dto.response.FeedbackListItemRes
import com.barostartbe.domain.feedback.dto.response.MenteeFeedbackDetailRes
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDateTime

@QueryUseCase
class FeedbackListQueryUseCase(

    private val assignmentRepository: AssignmentRepository,
    private val feedbackRepository: FeedbackRepository
) {

    // [멘토] 피드백 목록 조회
    fun getListByMentor(
        mentorId: Long,
        status: FeedbackStatus? = null,
        now: LocalDateTime = LocalDateTime.now()
    ): List<FeedbackListItemRes> {

        // 멘토 기준 제출된 과제 조회
        val assignments = assignmentRepository
            .findAllByMentorIdAndStatusInOrderBySubmittedAtDesc(
                mentorId,
                listOf(
                    AssignmentStatus.SUBMITTED,
                    AssignmentStatus.FEEDBACKED
                )
            )

        if (assignments.isEmpty()) { return emptyList() }

        // 상태 필터링
        val filteredAssignments = when (status) {
            null -> assignments
            FeedbackStatus.COMPLETED ->
                assignments.filter { it.status == AssignmentStatus.FEEDBACKED }

            FeedbackStatus.WAITING ->
                assignments.filter { it.status == AssignmentStatus.SUBMITTED && !it.dueDate.isBefore(now) }

            FeedbackStatus.DEADLINE ->
                assignments.filter { it.status == AssignmentStatus.SUBMITTED && it.dueDate.isBefore(now) }
        }

        // assignmentId 목록 추출
        val assignmentIds: List<Long> = filteredAssignments.mapNotNull { it.id }

        // 피드백 일괄 조회
        val feedbackMap =
            feedbackRepository.findAllByAssignmentIdIn(assignmentIds)
                .associateBy { it.assignment.id!! }

        return filteredAssignments.map { assignment ->
            FeedbackListItemRes.from(
                assignment = assignment,
                menteeName = assignment.mentee.name ?: "알 수 없음",
                now = now
            )
        }
    }

    // [멘티] 과제 기준 피드백 상세 조회
    fun getDetail(
        menteeId: Long,
        assignmentId: Long
    ): MenteeFeedbackDetailRes {

        // 과제 조회
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_NOT_FOUND) }

        // 멘티 본인 과제인지 권한 체크
        if (assignment.mentee.id != menteeId) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 과제에 연결된 피드백 조회
        val feedback = feedbackRepository.findByAssignmentId(assignmentId)
            ?: throw ServiceException(ErrorCode.FEEDBACK_NOT_FOUND)

        val mentor = assignment.mentor

        return MenteeFeedbackDetailRes(
            mentorName = mentor.name ?: "멘토",
            assignmentTitle = assignment.title,
            assignmentDueDate = assignment.dueDate, // 과제 마감일
            feedbackSummary = feedback.summary,
            feedbackContent = feedback.content,
            feedbackTime = feedback.createdAt!!      // 피드백 작성 시간
        )
    }
}