package com.barostartbe.domain.feedback.usecase

import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.dto.request.FeedbackCreateReq
import com.barostartbe.domain.feedback.entity.Feedback
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.transaction.annotation.Transactional

@CommandUseCase
class FeedbackCreateUseCase(

    private val assignmentRepository: AssignmentRepository,
    private val feedbackRepository: FeedbackRepository

) {

    // [멘토] 피드백 생성
    @Transactional
    fun create(
        mentorId: Long,
        assignmentId: Long,
        req: FeedbackCreateReq
    ): Long {

        // 과제 조회
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_NOT_FOUND) }

        // 권한 체크 (멘토 본인 과제인지)
        if (assignment.mentor.id != mentorId) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 과제 상태 체크 (제출된 과제만 피드백 가능)
        if (assignment.status != AssignmentStatus.SUBMITTED) {
            throw ServiceException(ErrorCode.ASSIGNMENT_NOT_SUBMITTED)
        }

        // 이미 피드백이 존재하는지 체크 (1:1 제약)
        if (feedbackRepository.existsByAssignmentId(assignmentId)) {
            throw ServiceException(ErrorCode.ASSIGNMENT_ALREADY_FEEDBACKED)
        }

        // 피드백 생성
        val feedback = Feedback.create(
            assignment = assignment,
            content = req.content,
            summary = req.summary
        )

        feedbackRepository.save(feedback)

        // 과제 상태 변경 (SUBMITTED -> FEEDBACKED)
        assignment.markFeedbacked()

        return feedback.id!!
    }
}
