package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileUsage
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.file.usecase.FileCommandUseCase
import com.barostartbe.domain.file.usecase.FileQueryUseCase
import com.barostartbe.global.annotation.CommandUseCase
import java.time.LocalDateTime

@CommandUseCase
class AssignmentCommandUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val fileCommandUseCase: FileCommandUseCase,
    private val fileQueryUseCase: FileQueryUseCase,
) {

    // [멘토] 과제 생성
    fun createAssignment(
        menteeId: Long,
        req: AssignmentCreateReq
    ): AssignmentCreateRes {

        // TODO (SECURITY)
        val mentorId = 1L       // 임시, SecurityContext에서 가져올 것

        val assignment = assignmentRepository.save(
            Assignment.create(
                mentorId = mentorId,
                menteeId = menteeId,
                req = req
            )
        )
        // 과제 자료(MATERIAL) 파일 매핑
        req.fileIds.forEach { fileId ->
            val file = fileQueryUseCase.findById(fileId)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignmentId = assignment.id!!,
                    fileId = file.id!!,
                    usage = AssignmentFileUsage.MATERIAL
                )
            )
        }

        return AssignmentCreateRes.from(assignment)
    }

    // [멘티] 과제 제출/재제출
    fun submitAssignment(req: AssignmentSubmitReq) {
        val assignment = assignmentRepository.findById(req.assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // TODO (SECURITY)
        // currentUserId == assignment.menteeId 검증

        assignment.submit(req.studyTime, req.memo)

        val submittedAt = req.submittedAtAdjustMinutes?.let { adjustMinutes ->
            LocalDateTime.now().plusMinutes(adjustMinutes.toLong())
        }

        assignmentFileRepository.deleteByAssignmentIdAndUsage(
            assignmentId = assignment.id!!,
            usage = AssignmentFileUsage.SUBMISSION
        )



        // 새 제출 파일 매핑
        req.files.forEach { fileReq ->
            val file = fileQueryUseCase.findById(fileReq.fileId)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignmentId = assignment.id!!,
                    fileId = file.id!!,
                    usage = AssignmentFileUsage.SUBMISSION
                )
            )
        }
    }

    /**
     * 멘티 제출물 삭제
     * - 제출 파일 삭제
     * - 과제 상태 NOT_SUBMIT으로 롤백
     */
    fun deleteSubmission(assignmentId: Long) {
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // TODO (SECURITY) 권한 검증

        assignmentFileRepository.deleteByAssignmentIdAndUsage(
            assignmentId = assignment.id!!,
            usage = AssignmentFileUsage.SUBMISSION
        )

        assignment.cancelSubmission()
    }
}
