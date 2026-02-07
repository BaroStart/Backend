package com.barostartbe.domain.assignment.dto.request

import com.barostartbe.domain.todo.dto.base.TimeSlot
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "[멘티] 과제 제출 요청 DTO")
data class AssignmentSubmitReq(

    @Schema(description = "과제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull
    val assignmentId: Long,

    @Schema(description = "학습 시간 구간", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull
    @field:Valid
    val timeSlot: TimeSlot,

    @Schema(description = "메모(선택)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 500, message = "memo는 500자를 초과할 수 없습니다")
    val memo: String? = null,

    @Schema(description = "제출 파일 URL 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Valid
    val fileUrls: List<String> = emptyList()
)
