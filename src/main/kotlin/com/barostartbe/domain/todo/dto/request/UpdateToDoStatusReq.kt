package com.barostartbe.domain.todo.dto.request

import com.barostartbe.domain.todo.entity.enums.Status
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Schema(description = "할 일 상태 변경 요청 DTO")
data class UpdateToDoStatusReq(

    @Schema(description = "할 일 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id는 필수입니다")
    val id: Long,

    @Schema(description = "할 일 상태", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status는 필수입니다")
    val status: Status,

    @Schema(description = "시작 시간", example = "2023-10-01T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val startTime: LocalDateTime?,

    @Schema(description = "종료 시간", example = "2023-10-01T11:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val endTime: LocalDateTime?,
) {
}
