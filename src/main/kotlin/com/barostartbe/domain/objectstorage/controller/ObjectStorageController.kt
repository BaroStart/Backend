package com.barostartbe.domain.objectstorage.controller

import com.barostartbe.domain.objectstorage.dto.response.PreAuthenticatedUrlResponse
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
<<<<<<< feat/#5
import org.springframework.web.bind.annotation.RequestMapping
=======
import org.springframework.web.bind.annotation.RequestParam
>>>>>>> develop
import org.springframework.web.bind.annotation.RestController


@RestController
class ObjectStorageController(
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) : ObjectStorageApi {

    // 파일 업로드용 PreAuthenticated URL 발급
    override fun getPreAuthenticatedUrl(fileName: String): ResponseEntity<ApiResponse<PreAuthenticatedUrlResponse>> =
        ApiResponse.success(
            SuccessCode.CREATE_OK,
            getPreAuthenticatedUrl.execute(
                fileName = fileName,
                purpose = PreAuthPurpose.UPLOAD
            )
        )

    // 파일 다운로드용 PreAuthenticated URL 발급
    override fun getDownloadPreAuthenticatedUrl(fileUrl: String): ResponseEntity<ApiResponse<PreAuthenticatedUrlResponse>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            getPreAuthenticatedUrl.execute(
                fileName = fileUrl,
                purpose = PreAuthPurpose.DOWNLOAD
            )
        )
}

