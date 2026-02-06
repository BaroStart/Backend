package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentFileRes
import com.barostartbe.domain.assignment.dto.response.AssignmentListRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.QueryUseCase
import java.time.LocalDate

@QueryUseCase
class AssignmentQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {

    fun getAssignments(
        subject: Subject? = null,
        status: AssignmentStatus? = null,
        dueDate: LocalDate? = null
    ): List<AssignmentListRes> {

        val assignments = assignmentRepository.findAll()    // TODO SECURITY

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
        val materials = assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.MATERIAL
            )
            .map { assignmentFile ->
                val downloadUrl = assignmentFile.filePath
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        getPreAuthenticatedUrl.execute(
                            it
                        ).url
                }
                AssignmentFileRes(
                    assignmentFileId = assignmentFile.id!!,
                    fileType = assignmentFile.fileType.name,
                    downloadUrl = null
                )
            }


        // 제출물 (멘티)
        val submissions = assignmentFileRepository
                .findAllByAssignmentIdAndFileType(
                    assignmentId = assignmentId,
                    fileType = AssignmentFileType.SUBMISSION
                )
                .map { assignmentFile ->
                    val downloadUrl = assignmentFile.filePath
                        .takeIf { it.isNotBlank() }
                        ?.let {
                            getPreAuthenticatedUrl.execute(
                                it
                            ).url
                        }

                    AssignmentFileRes(
                        assignmentFileId = assignmentFile.id!!,
                        fileType = assignmentFile.fileType.name,
                        downloadUrl = null
                    )
                }

        return AssignmentDetailRes.from(
            assignment = assignment,
            materials = materials,
            submissions = submissions
        )
    }
}
