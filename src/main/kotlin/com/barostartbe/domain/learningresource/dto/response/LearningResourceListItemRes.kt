package com.barostartbe.domain.learningresource.dto.response

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.learningresource.entity.LearningResource
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "학습 자료 목록 조회 응답 DTO")
data class LearningResourceListItemRes(

    @Schema(description = "학습 자료 ID", example = "12")
    val id: Long,

    @Schema(description = "파일명", example = "수리논술_1.pdf")
    val fileName: String,

    @Schema(description = "과목명", example = "MATH")
    val subject: Subject,

    @Schema(description = "파일 다운로드 URL (클릭 시 바로 다운로드)", example = "https://objectstorage.../file.pdf")
    val fileUrl: String,

    @Schema(description = "파일 크기 (byte 단위, 프론트에서 KB/MB 변환)", example = "2048576")
    val fileSize: Long,

    @Schema(description = "파일 등록 일시", example = "2025-01-02T10:30:00")
    val createdAt: LocalDateTime
) {
    companion object {
        // 목록 조회 전용 매핑
        fun from(entity: LearningResource): LearningResourceListItemRes =
            LearningResourceListItemRes(
                id = entity.id!!,
                fileName = entity.fileName,
                subject = entity.subject,
                fileUrl = entity.fileUrl,
                fileSize = entity.fileSize,
                createdAt = entity.createdAt!!
            )
    }
}