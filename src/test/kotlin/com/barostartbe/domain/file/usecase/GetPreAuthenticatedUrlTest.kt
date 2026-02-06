package com.barostartbe.domain.objectstorage.usecase

import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GetPreAuthenticatedUrlTest : DescribeSpec({

    val objectStorage = mockk<ObjectStorage>(relaxed = true)

    val bucketName = "test-bucket"
    val bucketNamespace = "test-namespace"

    val useCase = GetPreAuthenticatedUrl(
        objectStorage = objectStorage,
        bucketName = bucketName,
        bucketNamespace = bucketNamespace
    )

    describe("GetPreAuthenticatedUrl") {
        context("Pre-Authenticated URL 생성 요청 시") {

            val objectName = "assignments/test.jpg"
            val accessUri = "/p/random/b/$bucketName/o/$objectName"

            val preAuthRequest = PreauthenticatedRequest.builder()
                .accessUri(accessUri)
                .build()

            val response = CreatePreauthenticatedRequestResponse.builder()
                .preauthenticatedRequest(preAuthRequest)
                .build()

            it("전체 URL을 반환한다") {

                every {
                    objectStorage.createPreauthenticatedRequest(any())
                } returns response

                every { objectStorage.endpoint } returns "https://objectstorage.test.com"

                val result = useCase.execute(
                    objectName,
                    PreAuthPurpose.DOWNLOAD
                )

                result.url shouldBe "https://objectstorage.test.com$accessUri"

            }
        }
    }
})

