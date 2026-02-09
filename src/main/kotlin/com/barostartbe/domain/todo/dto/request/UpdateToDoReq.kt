package com.barostartbe.domain.todo.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "할 일 수정 요청 DTO")
data class UpdateToDoReq(

    @Schema(description = "할 일 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id는 필수입니다")
    val id: Long,

    @Schema(description = "할 일 제목", example = "국어 문제 풀기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    val title: String,

    @Schema(description = "시작 시간", example = "2023-10-01T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val startTime: LocalDateTime?,

    @Schema(description = "종료 시간", example = "2023-10-01T11:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val endTime: LocalDateTime?,
) {
}
