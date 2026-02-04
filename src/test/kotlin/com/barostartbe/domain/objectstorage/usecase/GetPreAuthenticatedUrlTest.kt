package com.barostartbe.domain.objectstorage.usecase

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetPreAuthenticatedUrlImplTest : DescribeSpec({

    val objectStorage = mockk<ObjectStorage>()
    val bucketName = "test-bucket"
    val namespaceName = "test-namespace"
    val region = "ap-seoul-1"
    val endpoint = "https://$namespaceName.objectstorage.$region.oci.customer-oci.com"

    val getPreAuthenticatedUrlImpl = GetPreAuthenticatedUrl(objectStorage, bucketName, namespaceName)

    describe("GetPreAuthenticatedUrl") {
        context("Presigned URL 생성을 요청할 때") {
            val fileName = "test.jpg"
            val accessUri = "/p/some-random-string/b/bucket/o/test.jpg"

            val request = PreauthenticatedRequest.builder().accessUri(accessUri).build()
            val response = CreatePreauthenticatedRequestResponse.builder()
                .preauthenticatedRequest(request)
                .build()

            every { objectStorage.createPreauthenticatedRequest(any()) } returns response
            every { objectStorage.endpoint } returns endpoint

            it("객체 업로드 권한이 있는 전체 URL을 반환한다") {
                val result = getPreAuthenticatedUrlImpl.execute(fileName)

                result.url shouldBe "$endpoint$accessUri"
                verify { objectStorage.createPreauthenticatedRequest(any()) }
            }
        }
    }
})
