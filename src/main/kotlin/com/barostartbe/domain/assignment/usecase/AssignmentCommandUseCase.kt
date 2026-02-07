package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.net.URI
import java.time.LocalDateTime

@CommandUseCase
class AssignmentCommandUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {

    // [멘토] 과제 생성
    fun createAssignment(mentorId: Long, menteeId: Long, req: AssignmentCreateReq): AssignmentCreateRes {

        val mentor = mentorRepository.findById(mentorId)
            .orElseThrow { ServiceException(ErrorCode.USER_NOT_FOUND) }

        val mentee = menteeRepository.findById(menteeId)
            .orElseThrow { ServiceException(ErrorCode.USER_NOT_FOUND) }

        mentorMenteeMappingRepository
            .findByMentorAndMentee(mentor, mentee)
            ?: throw ServiceException(ErrorCode.UNMATCHED_PAIR)

        // 설 스터디 컬럼 context 병합
        val mergedContent = buildString {
            req.seolStudyColumn?.let {
                append("[SeolStudy Column]\n")
                append(it)
                append("\n\n")
            }
            req.content?.let { append(it) }
        }.ifBlank { null } // 둘 다 없으면 null

        val assignment = assignmentRepository.save(
            Assignment.create(
                mentor = mentor,
                mentee = mentee,
                req = req.copy(content = mergedContent)
            )
        )

        // 과제 자료(MATERIAL) 파일 매핑
        req.fileUrls.forEach { url ->
            val parsed = parseFileUrl(url)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignment = assignment,
                    fileType = AssignmentFileType.MATERIAL,
                    fileName = parsed.fileName,
                    fileUrl = getPreAuthenticatedUrl.execute(parsed.filePath).url
                )
            )
        }

        return AssignmentCreateRes.from(assignment)
    }

    // [멘티] 과제 제출/재제출
    fun submitAssignment(menteeId: Long, assignmentId: Long, req: AssignmentSubmitReq) {
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        val mentee = menteeRepository.findById(menteeId)
            .orElseThrow { ServiceException(ErrorCode.USER_NOT_FOUND) }

        if (assignment.mentee.id != mentee.id) {
            throw ServiceException(ErrorCode.NOT_FOUND)
        }

        assignment.submit(
            startTime = null,
            endTime = LocalDateTime.now(),
            memo = req.memo,
            submittedAt = LocalDateTime.now()
        )

        // 기존 제출 파일 전체 삭제
        assignmentFileRepository.deleteByAssignmentIdAndFileType(
            assignmentId = assignment.id!!,
            fileType = AssignmentFileType.SUBMISSION
        )

        // 새 제출 파일 매핑
        req.fileUrls.forEach { url ->
            val parsed = parseFileUrl(url)

            assignmentFileRepository.save(
                AssignmentFile(
                    assignment = assignment,
                    fileType = AssignmentFileType.SUBMISSION,
                    fileName = parsed.fileName,
                    fileUrl = getPreAuthenticatedUrl.execute(parsed.filePath).url
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
