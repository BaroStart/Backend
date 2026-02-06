package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentMaterialRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AssignmentMaterialQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository
) {

    /**
     * 멘토가 등록한 모든 학습자료 조회
     */
    fun getAllMaterialsByMentor(
        mentorId: Long,
        subject: Subject? = null
    ): List<AssignmentMaterialRes> {

        // 멘토의 모든 과제 조회
        val assignments = assignmentRepository.findAllByMentorId(mentorId)
            .filter { subject == null || it.subject == subject }

        if (assignments.isEmpty()) {
            return emptyList()
        }

        val assignmentMap = assignments.associateBy { it.id!! }
        val assignmentIds = assignments.map { it.id!! }

        // 과제들의 모든 MATERIAL 파일 조회
        val materials = assignmentIds.flatMap { assignmentId ->
            assignmentFileRepository.findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.MATERIAL
            )
        }

        return materials.mapNotNull { file ->
            val assignment = assignmentMap[file.assignmentId] ?: return@mapNotNull null

            AssignmentMaterialRes(
                assignmentFileId = file.id!!,
                assignmentId = file.assignmentId,
                assignmentTitle = assignment.title,
                subject = assignment.subject,
                fileName = file.fileName,
                filePath = file.filePath
            )
        }
    }
}