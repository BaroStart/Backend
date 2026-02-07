package com.barostartbe.domain.todo.dto.request

import com.barostartbe.domain.todo.dto.base.TimeSlot
import com.barostartbe.domain.todo.entity.enums.Status
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "할 일 상태 변경 요청 DTO")
data class UpdateToDoStatusReq(

    @Schema(description = "할 일 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id는 필수입니다")
    val id: Long,

    @Schema(description = "할 일 상태", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status는 필수입니다")
    val status: Status,

    @Schema(description = "시간 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val timeList: List<TimeSlot>?
) {
}
