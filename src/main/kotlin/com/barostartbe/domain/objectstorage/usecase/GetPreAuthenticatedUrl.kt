package com.barostartbe.domain.objectstorage.usecase

import com.barostartbe.domain.objectstorage.dto.response.PreAuthenticatedUrlResponse
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
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

    fun execute(fileName: String): PreAuthenticatedUrlResponse {
        val uniqueFileName = generateUniqueFileName(fileName)
        val expirationTime = calculateExpirationTime()

        val details = buildPreAuthenticatedRequestDetails(uniqueFileName, expirationTime)
        val request = buildPreAuthenticatedRequest(details)

        val response = objectStorage.createPreauthenticatedRequest(request)
        val fullUrl = buildFullUrl(response.preauthenticatedRequest.accessUri)

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

    private fun calculateExpirationTime(): Date =
        Date.from(Instant.now().plus(PRESIGNED_URL_EXPIRATION_MINUTES, ChronoUnit.MINUTES))

    private fun buildPreAuthenticatedRequestDetails(
        fileName: String,
        expirationTime: Date
    ): CreatePreauthenticatedRequestDetails =
        CreatePreauthenticatedRequestDetails.builder().apply {
            name("$PRESIGNED_REQUEST_NAME_PREFIX${UUID.randomUUID()}")
            objectName(fileName)
            accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
            timeExpires(expirationTime)
        }.build()

    private fun buildPreAuthenticatedRequest(
        details: CreatePreauthenticatedRequestDetails
    ): CreatePreauthenticatedRequestRequest =
        CreatePreauthenticatedRequestRequest.builder().apply {
            namespaceName(bucketNamespace)
            bucketName(bucketName)
            createPreauthenticatedRequestDetails(details)
        }.build()

    private fun buildFullUrl(accessUri: String): String =
        "${objectStorage.endpoint}$accessUri"
}