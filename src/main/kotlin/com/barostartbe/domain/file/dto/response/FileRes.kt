package com.barostartbe.domain.file.dto.response

import com.barostartbe.domain.file.entity.File
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 파일 정보 응답 DTO
 * 파일 정보를 클라이언트에 반환할 때 사용
 */
@Schema(description = "파일 정보 응답 DTO")
data class FileRes(

    @Schema(description = "파일 ID")
    val id: Long,

    @Schema(description = "파일명")
    val fileName: String,

    @Schema(description = "파일 Object Storage 경로 (objectKey)")
    val filePath: String
) {

    companion object {

        fun from(file: File): FileRes {
            return FileRes(
                id = file.id!!,
                fileName = file.fileName!!,
                filePath = file.filePath!!
            )
        }
    }
}