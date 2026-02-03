package com.barostartbe.domain.objectstorage.controller

import com.barostartbe.domain.objectstorage.dto.response.PreAuthenticatedUrlResponse
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@RequestMapping("/storages")
@Tag(name = "Object Storage API", description = "OCI Object Storage 관리 API")
interface ObjectStorageApi {

    @Operation(
        summary = "Pre-Authenticated Url 발급",
        description = "OCI Object Storage에 파일 업로드를 위한 Pre-Authenticated Url을 발급합니다."
    )
    @PostMapping("/pre-authenticated-url")
    fun getPreAuthenticatedUrl(
        @NotBlank
        @Parameter(description = "버킷에 업로드할 파일 이름 및 경로", example = "profile/{username}.jpg")
        @RequestParam fileName: String
    ): ResponseEntity<ApiResponse<PreAuthenticatedUrlResponse>>
}
