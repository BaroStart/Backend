package com.barostartbe.domain.assignment.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "[멘티] 과제 제출 요청 DTO")
data class AssignmentSubmitReq(

    @Schema(description = "과제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull
    val assignmentId: Long,

    @Schema(
        description = "공부 시작 시각 (2025-02-15T19:00:00)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val startTime: LocalDateTime? = null,

    @Schema(
        description = "공부 종료 시각 (2025-02-15T21:30:00)",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val endTime: LocalDateTime? = null,

    @Schema(description = "메모(선택)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 500, message = "memo는 500자를 초과할 수 없습니다")
    val memo: String? = null,

    @Schema(description = "제출 파일 URL 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Valid
    val fileUrls: List<String> = emptyList()
)
