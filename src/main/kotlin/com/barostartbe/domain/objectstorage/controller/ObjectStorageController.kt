package com.barostartbe.domain.objectstorage.controller

import com.barostartbe.domain.objectstorage.dto.response.PreAuthenticatedUrlResponse
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ObjectStorageController(
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) : ObjectStorageApi {

    // 파일 업로드용 PreAuthenticated URL 발급
    override fun getPreAuthenticatedUrl(@RequestParam fileName: String): ResponseEntity<ApiResponse<PreAuthenticatedUrlResponse>> =
        ApiResponse.success(SuccessCode.CREATE_OK, getPreAuthenticatedUrl.execute(fileName))
}

