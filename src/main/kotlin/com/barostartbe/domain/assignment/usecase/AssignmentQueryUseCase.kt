package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentFileRes
import com.barostartbe.domain.assignment.dto.response.AssignmentListRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileUsage
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.file.usecase.FileQueryUseCase
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.barostartbe.global.annotation.QueryUseCase
import java.time.LocalDate

@QueryUseCase
class AssignmentQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val fileQueryUseCase: FileQueryUseCase,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {

    fun getAssignments(
        subject: Subject? = null,
        status: AssignmentStatus? = null,
        dueDate: LocalDate? = null
    ): List<AssignmentListRes> {

        // TODO SECURITY
        // val currentUserId = securityUtils.currentUserId()
        // val role = securityUtils.currentRole()

        val assignments = assignmentRepository.findAll() // 임시
        // TODO SECURITY
        /*
        val assignments = when (role) {
            "MENTOR" -> assignmentRepository.findAllByMentorId(currentUserId)
            "MENTEE" -> assignmentRepository.findAllByMenteeId(currentUserId)
            else -> emptyList()
        }
        */

        return assignments
            .asSequence()
            .filter { subject == null || it.subject == subject }
            .filter { status == null || it.status == status }
            .filter { dueDate == null || it.dueDate == dueDate }
            .map { AssignmentListRes.from(it) }
            .toList()
    }


    fun getAssignmentDetail(assignmentId: Long): AssignmentDetailRes {

        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // TODO SECURITY: 로그인 사용자 권한 검증

        // 학습자료 (멘토)
        val materials: List<AssignmentFileRes> =
            assignmentFileRepository
                .findAllByAssignmentIdAndUsage(
                    assignmentId = assignmentId,
                    usage = AssignmentFileUsage.MATERIAL
                )
                .map { assignmentFile ->
                    val file = fileQueryUseCase.findById(assignmentFile.fileId)


                    val preAuthUrl = file.filePath
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            getPreAuthenticatedUrl.execute(
                                it,
                                PreAuthPurpose.DOWNLOAD
                            )
                        }

                    AssignmentFileRes(
                        assignmentFileId = assignmentFile.id!!,
                        fileId = file.id!!,
                        usage = assignmentFile.usage.name,
                        downloadUrl = preAuthUrl?.url
                    )
                }


        // 제출물 (멘티)
        val submissions: List<AssignmentFileRes> =
            assignmentFileRepository
                .findAllByAssignmentIdAndUsage(
                    assignmentId = assignmentId,
                    usage = AssignmentFileUsage.SUBMISSION
                )
                .map { assignmentFile ->
                    val file = fileQueryUseCase.findById(assignmentFile.fileId)

                    val preAuthUrl = file.filePath
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            getPreAuthenticatedUrl.execute(
                                it,
                                PreAuthPurpose.DOWNLOAD
                            )
                        }

                    AssignmentFileRes(
                        assignmentFileId = assignmentFile.id!!,
                        fileId = file.id!!,
                        usage = assignmentFile.usage.name,
                        downloadUrl = preAuthUrl?.url
                    )
                }

        return AssignmentDetailRes.from(
            assignment = assignment,
            materials = materials,
            submissions = submissions
        )
    }
}