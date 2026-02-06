package com.barostartbe.domain.objectstorage.usecase

import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class ObjectStoragePreAuthenticatedUrlTest : DescribeSpec({

    val objectStorage = mockk<ObjectStorage>(relaxed = true)

    val bucketName = "test-bucket"
    val bucketNamespace = "test-namespace"

    val region = "ap-seoul-1"
    val endpoint = "https://$bucketNamespace.objectstorage.$region.oci.customer-oci.com"

    val useCase = GetPreAuthenticatedUrl(
        objectStorage = objectStorage,
        bucketName = bucketName,
        bucketNamespace = bucketNamespace,
    )

    beforeEach {
        clearMocks(objectStorage)
    }

    describe("GetPreAuthenticatedUrl") {
        context("Pre-Authenticated URL 생성을 요청할 때") {

            val objectName = "assignments/2026/02/06/test.jpg" // filePath/objName 성격
            val accessUri = "/p/some-random-string/n/$bucketNamespace/b/$bucketName/o/$objectName"

            val request = PreauthenticatedRequest.builder()
                .accessUri(accessUri)
                .build()

            val response = CreatePreauthenticatedRequestResponse.builder()
                .preauthenticatedRequest(request)
                .build()

            it("purpose에 맞는 전체 URL을 반환한다") {
                every { objectStorage.createPreauthenticatedRequest(any()) } returns response
                every { objectStorage.endpoint } returns endpoint

                val result = useCase.execute(
                    fileName = objectName,
                    purpose = PreAuthPurpose.DOWNLOAD
                )

                result.url shouldBe "$endpoint$accessUri"

                verify(exactly = 1) { objectStorage.createPreauthenticatedRequest(any()) }
                verify(exactly = 1) { objectStorage.endpoint }
            }
        }
    }
})
