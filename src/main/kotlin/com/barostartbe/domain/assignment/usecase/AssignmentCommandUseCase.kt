package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.net.URI
import java.time.LocalDateTime

@CommandUseCase
class AssignmentCommandUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
) {

    // [멘토] 과제 생성
    fun createAssignment(menteeId: Long, req: AssignmentCreateReq): AssignmentCreateRes {

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
        req.fileUrls.forEach { url ->
            val parsed = parseFileUrl(url)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignmentId = assignment.id!!,
                    fileType = AssignmentFileType.MATERIAL,
                    fileName = parsed.fileName,
                    filePath = parsed.filePath
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

        // 시간 검증
        if (req.startTime != null && req.endTime != null) {
            require(!req.endTime.isBefore(req.startTime)) {
                "endTime은 startTime보다 빠를 수 없습니다"
            }
        }

        assignment.submit(
            startTime = req.startTime,
            endTime = req.endTime,
            memo = req.memo,
            submittedAt = LocalDateTime.now()
        )

        // 기존 제출 파일 전체 삭제 후 재등록
        assignmentFileRepository.deleteByAssignmentIdAndFileType(
            assignmentId = assignment.id!!,
            fileType = AssignmentFileType.SUBMISSION
        )

        // 새 제출 파일 매핑
        req.fileUrls.forEach { url ->
            val parsed = parseFileUrl(url)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignmentId = assignment.id!!,
                    fileType = AssignmentFileType.SUBMISSION,
                    fileName = parsed.fileName,
                    filePath = parsed.filePath
                )
            )
        }
    }

    /**
     * 멘티 제출물 삭제 (제출 취소)
     */
    fun deleteSubmission(assignmentId: Long) {
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // TODO (SECURITY) 권한 검증

        assignmentFileRepository.deleteByAssignmentIdAndFileType(
            assignmentId = assignment.id!!,
            fileType = AssignmentFileType.SUBMISSION
        )
        assignment.cancelSubmission()
    }


    private fun parseFileUrl(url: String): ParsedFile {
        val uri = runCatching { URI(url) }
            .getOrElse { throw ServiceException(ErrorCode.INVALID_FILE_URL) }

        val path = uri.path
            ?: throw ServiceException(ErrorCode.INVALID_FILE_URL)

        val fileName = path.substringAfterLast("/")

        if (fileName.isBlank()) {
            throw ServiceException(ErrorCode.INVALID_FILE_URL)
        }

        return ParsedFile(
            fileName = fileName,
            filePath = path
        )
    }

    private data class ParsedFile(
        val fileName: String,
        val filePath: String
    )
}
