package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.error.AssignmentFileNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AssignmentFileDownloadUseCase(
    private val assignmentFileRepository: AssignmentFileRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {

    fun execute(assignmentFileId: Long): String {
        val assignmentFile = assignmentFileRepository.findById(assignmentFileId)
            .orElseThrow { AssignmentFileNotFoundException() }

        return getPreAuthenticatedUrl
            .execute(fileName = assignmentFile.filePath)
            .url
    }
}

