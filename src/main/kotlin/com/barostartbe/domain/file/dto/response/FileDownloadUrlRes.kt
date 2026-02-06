package com.barostartbe.domain.file.dto.response

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 파일 다운로드 URL 응답 DTO
 */
@Schema(description = "파일 다운로드 URL 응답 DTO")
data class FileDownloadUrlRes(

    @Schema(description = "파일 ID")
    val fileId: Long,

    @Schema(description = "파일명")
    val fileName: String,

    @Schema(description = "다운로드 가능한 Pre-Authenticated URL")
    val downloadUrl: String
)