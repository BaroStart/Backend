package com.barostartbe.domain.file.controller

import com.barostartbe.domain.file.dto.request.FileUploadReq
import com.barostartbe.domain.file.dto.response.FileDownloadUrlRes
import com.barostartbe.domain.file.dto.response.FileRes
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RequestMapping("/api/v1/files")
@Tag(name = "File API", description = "파일 관리 API")
interface FileApi {

    // 파일 업로드 (OCI Object Storage에 업로드 후 메타데이터 저장)
    @PostMapping
    @Operation(summary = "파일 업로드", description = "파일을 OCI Object Storage에 업로드하고 메타데이터를 저장합니다.")
    fun uploadFile(
        @Valid @RequestBody request: FileUploadReq
    ): ResponseEntity<ApiResponse<FileRes>>

    // ID로 특정 파일 정보 조회
    @GetMapping("/{id}")
    @Operation(summary = "파일 정보 조회", description = "ID로 특정 파일의 정보를 조회합니다.")
    fun getFileById(
        @Parameter(description = "조회할 파일 ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<FileRes>>

    // 파일 다운로드 URL 발급 (Pre-Authenticated URL)
    @GetMapping("/{id}/download-url")
    @Operation(summary = "파일 다운로드 URL 발급", description = "파일 다운로드를 위한 Pre-Authenticated URL을 발급합니다.")
    fun getDownloadUrl(
        @Parameter(description = "다운로드할 파일 ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<FileDownloadUrlRes>>

    // 파일 삭제 (Soft Delete)
    @DeleteMapping("/{id}")
    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다.")
    fun deleteFile(
        @Parameter(description = "삭제할 파일 ID", required = true, example = "1")
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>>

    // 모든 파일 목록 조회
    @GetMapping
    @Operation(summary = "파일 목록 조회", description = "모든 파일 목록을 조회합니다. 삭제된 파일은 제외됩니다.")
    fun getAllFiles(): ResponseEntity<ApiResponse<List<FileRes>>>
}