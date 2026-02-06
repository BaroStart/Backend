package com.barostartbe.domain.file.usecase

import com.barostartbe.domain.file.entity.File
import com.barostartbe.domain.file.repository.FileRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import org.springframework.beans.factory.annotation.Value


@CommandUseCase
class FileCommandUseCase(
    private val fileRepository: FileRepository,
    private val objectStorage: ObjectStorage,
    @Value("\${oci.bucket.namespace}")
    private val namespaceName: String,
    @Value("\${oci.bucket.name}")
    private val bucketName: String
) {

    // 파일 메타데이터 생성
    fun createFile(
        fileName: String,
        filePath: String
    ): File {
        return fileRepository.save(
            File(
                fileName = fileName,
                filePath = filePath
            )
        )
    }

    // 단일 파일 삭제
    fun delete(file: File) {
        // OCI 파일 삭제
        objectStorage.deleteObject(
            DeleteObjectRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .objectName(file.filePath!!)
                .build()
        )

        // DB 메타데이터 삭제
        fileRepository.delete(file)
    }

    /**
     * 여러 파일 일괄 삭제
     * - 과제 삭제 / 제출물 삭제 시 사용
     */
    fun deleteAll(files: List<File>) {
        files.forEach { file ->
            objectStorage.deleteObject(
                DeleteObjectRequest.builder()
                    .namespaceName(namespaceName)
                    .bucketName(bucketName)
                    .objectName(file.filePath!!)
                    .build()
            )
        }

        fileRepository.deleteAll(files)
    }
}
