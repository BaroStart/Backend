package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.domain.assignment.entity.enums.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enums.Subject // [ADD] 과목 enums
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.web.multipart.MultipartFile

@CommandUseCase
class AssignmentMaterialUploadUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {

    // 학습자료 업로드
    fun uploadMaterial(
        assignmentId: Long,
        subject: Subject, // [ADD] 과목 파라미터 추가
        file: MultipartFile
    ) {
        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        val originalFileName = file.originalFilename
            ?: throw ServiceException(ErrorCode.INVALID_FILE)

        val uploadUrl = getPreAuthenticatedUrl.execute(
            "materials/$originalFileName"
        )

        // 학습자료 메타데이터 저장
        assignmentFileRepository.save(
            AssignmentFile(
                assignment = assignment,
                fileType = AssignmentFileType.MATERIAL,
                fileName = originalFileName,
                fileUrl = uploadUrl.url
            )
        )
    }
}
