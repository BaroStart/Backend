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

@RequestMapping("/api/v1/storages")
@Tag(name = "Object Storage API", description = "OCI Object Storage 관리 API")
interface ObjectStorageApi {

    /**
     * 파일 업로드용 Pre-Authenticated URL 발급
     * - 프론트에서 PUT 업로드
     */
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


    /**
     * 파일 다운로드용 Pre-Authenticated URL 발급
     * - DB에 저장된 fileUrl 기준
     */
    @Operation(
        summary = "파일 다운로드용 Pre-Authenticated URL 발급",
        description = "OCI Object Storage에 저장된 파일 다운로드용 Pre-Authenticated URL을 발급합니다."
    )
    @PostMapping("/pre-authenticated-url/download")
    fun getDownloadPreAuthenticatedUrl(
        @NotBlank
        @Parameter(description = "DB에 저장된 파일 URL")
        @RequestParam fileUrl: String
    ): ResponseEntity<ApiResponse<PreAuthenticatedUrlResponse>>
}
