package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentFileRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeListRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDate

@QueryUseCase
class AssignmentQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {
    // [멘티] 과제 목록 조회
    fun getAssignmentsByMentee(
        menteeId: Long,
        subject: Subject? = null,
        dueDate: LocalDate? = null
    ): List<AssignmentMenteeListRes> {

        val assignments = assignmentRepository.findAllByMentee_Id(menteeId)

        return assignments
            .asSequence()
            .filter { subject == null || it.subject == subject }
            .filter { dueDate == null || it.dueDate.toLocalDate() == dueDate }
            .map { AssignmentMenteeListRes.from(it) }
            .toList()
    }


    // [멘티] 과제 상세 조회
    fun getAssignmentDetailByMentee(
        assignmentId: Long,
        menteeId: Long
    ): AssignmentMenteeDetailRes {

        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // 멘티 본인 과제만 조회
        if (assignment.mentee.id != menteeId) {
            throw ServiceException(ErrorCode.ASSIGNMENT_PERMISSION_DENIED)
        }

        // 학습 자료 (멘토 업로드)
        val materials = assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.MATERIAL
            )
            .map { file ->
                val downloadUrl = file.url

                AssignmentFileRes(
                    assignmentFileId = file.id!!,
                    fileType = file.fileType.name,
                    downloadUrl = downloadUrl
                )
            }

        // 제출물 (멘티 업로드)
        val submissions = assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.SUBMISSION
            )
            .map { file ->
                val downloadUrl = file.url

                AssignmentFileRes(
                    assignmentFileId = file.id!!,
                    fileType = file.fileType.name,
                    downloadUrl = downloadUrl
                )
            }

        return AssignmentMenteeDetailRes(
            assignmentId = assignment.id!!,
            title = assignment.title,
            subject = assignment.subject,
            dueDate = assignment.dueDate,
            goal = assignment.goal,
            content = assignment.content,
            materials = materials,
            submittedAt = assignment.submittedAt,
            memo = assignment.memo,
            submissions = submissions
        )
    }

    fun getAssignmentDetail(assignmentId: Long) {}

    fun checkCompletedAssignmentExists(menteeId: Long): Boolean =
    assignmentRepository.existsByStatusAndMentee_id(AssignmentStatus.SUBMITTED, menteeId)
}
