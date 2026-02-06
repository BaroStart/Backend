package com.barostartbe.domain.todo.dto.request

import com.barostartbe.domain.todo.dto.base.TimeSlot
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "할 일 수정 요청 DTO")
data class UpdateToDoReq(

    @Schema(description = "할 일 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id는 필수입니다")
    val id: Long,

    @Schema(description = "할 일 제목", example = "국어 문제 풀기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    val title: String,

    @Schema(description = "시간 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val timeList: List<TimeSlot>?
) {
}
