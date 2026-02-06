package com.barostartbe.domain.objectstorage.usecase

import com.barostartbe.domain.objectstorage.dto.response.PreAuthenticatedUrlResponse
import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


private const val PRESIGNED_URL_EXPIRATION_MINUTES = 15L
private const val PRESIGNED_REQUEST_NAME_PREFIX = "PAR_Request_"

@Service
class GetPreAuthenticatedUrl(
    private val objectStorage: ObjectStorage,
    @Value("\${oci.bucket.name}") private val bucketName: String,
    @Value("\${oci.bucket.namespace}") private val bucketNamespace: String,
) {

    /**
     * Pre-Authenticated URL 발급
     */
    fun execute(
        fileName: String,
        purpose: PreAuthPurpose
    ): PreAuthenticatedUrlResponse {

        val objectName = when (purpose) {
            PreAuthPurpose.UPLOAD -> generateUniqueObjectName(fileName)
            PreAuthPurpose.DOWNLOAD -> extractObjectName(fileName)
        }

        val expirationTime = Date.from(
            Instant.now().plus(PRESIGNED_URL_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
        )

        val accessType = when (purpose) {
            PreAuthPurpose.UPLOAD ->
                CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite

            PreAuthPurpose.DOWNLOAD ->
                CreatePreauthenticatedRequestDetails.AccessType.ObjectRead
        }

        val details = CreatePreauthenticatedRequestDetails.builder()
            .name("$PRESIGNED_REQUEST_NAME_PREFIX${UUID.randomUUID()}")
            .objectName(objectName)
            .accessType(accessType)
            .timeExpires(expirationTime)
            .build()

        val request = CreatePreauthenticatedRequestRequest.builder()
            .namespaceName(bucketNamespace)
            .bucketName(bucketName)
            .createPreauthenticatedRequestDetails(details)
            .build()

        val response = objectStorage.createPreauthenticatedRequest(request)

        val fullUrl = "${objectStorage.endpoint}${response.preauthenticatedRequest.accessUri}"

        return PreAuthenticatedUrlResponse(fullUrl)
    }

    private fun generateUniqueFileName(fileName: String): String {
        val uuid = UUID.randomUUID()

        return if ('/' in fileName) {
            "${fileName.substringBeforeLast('/')}/${uuid}_${fileName.substringAfterLast('/')}"
        } else {
            "${uuid}_$fileName"
        }
    }

    /**
     * 업로드 시 objectName 중복 방지 (UUID prefix)
     */
    private fun generateUniqueObjectName(originalName: String): String {
        val uuid = UUID.randomUUID()

        return if ("/" in originalName) {
            "${originalName.substringBeforeLast('/')}/${uuid}_${originalName.substringAfterLast('/')}"
        } else {
            "${uuid}_$originalName"
        }
    }

    /**
     * 다운로드 시 DB에 저장된 file.url -> objectName 추출
     */
    private fun extractObjectName(fileUrl: String): String {
        return if (fileUrl.startsWith("http")) {
            URI(fileUrl).path.removePrefix("/")
        } else {
            fileUrl
        }
    }
}