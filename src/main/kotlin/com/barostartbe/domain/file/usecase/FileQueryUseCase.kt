package com.barostartbe.domain.file.usecase

import com.barostartbe.domain.file.entity.File
import com.barostartbe.domain.file.error.FileNotFoundException
import com.barostartbe.domain.file.repository.FileRepository
import com.barostartbe.global.annotation.QueryUseCase
import org.springframework.data.repository.findByIdOrNull

/**
 * TODO (Security)
 * - 조회 권한 검증 로직 추가
 * - 본인이 업로드했거나 접근 권한이 있는 파일만 조회 가능
 */
@QueryUseCase
class FileQueryUseCase(
    private val fileRepository: FileRepository
) {

    /**
     * 파일 ID로 파일 조회
     * TODO (Security)
     * - 파일 접근 권한 검증 추가
     */
    fun findById(fileId: Long): File {
        val file = fileRepository.findByIdOrNull(fileId)
            ?: throw FileNotFoundException()

        return file
    }

    /**
     * 여러 파일 ID로 파일 목록 조회
     *
     * TODO (Security)
     * - 각 파일에 대한 접근 권한 검증
     * - 권한이 없는 파일은 목록에서 제외
     */


    /**
     * 모든 파일 조회
     *
     * TODO (Security): 사용자 권한 기반 필터링
     */
    fun findAll(): List<File> {
        return fileRepository.findAll()
    }

    /**
     * TODO
     * 과제 ID로 관련 파일 조회
     *
     * AssignmentFile과 조인하여 조회
     *
     * fun findAllByAssignmentId(assignmentId: Long): List<File> {
     *     return fileRepository.findAllByAssignmentFiles_Assignment_Id(assignmentId)
     * }
     */
}
