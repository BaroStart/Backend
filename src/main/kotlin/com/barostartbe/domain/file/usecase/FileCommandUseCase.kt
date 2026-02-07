package com.barostartbe.domain.file.usecase

import com.barostartbe.global.annotation.CommandUseCase
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import org.springframework.beans.factory.annotation.Value


@CommandUseCase
class FileCommandUseCase(
    private val objectStorage: ObjectStorage,
    @Value("\${oci.bucket.namespace}")
    private val namespaceName: String,
    @Value("\${oci.bucket.name}")
    private val bucketName: String
) {
    // OCI Object Storage에서 파일 삭제
    fun deleteObject(filePath: String) {
        objectStorage.deleteObject(
            DeleteObjectRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .objectName(filePath)
                .build()
        )
    }
}

