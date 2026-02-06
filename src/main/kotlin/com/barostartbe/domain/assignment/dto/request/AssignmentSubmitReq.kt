package com.barostartbe.domain.assignment.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "[멘티] 과제 제출 요청 DTO")
data class AssignmentSubmitReq(

    @Schema(description = "과제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull
    val assignmentId: Long,

    @Schema(description = "공부 시간(분)", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "studyTime은 필수입니다")
    @field:Min(0, message = "studyTime은 0 이상이어야 합니다")
    @field:Max(24 * 60, message = "studyTime은 하루(1440분)를 초과할 수 없습니다")

    val studyTime: Int,

    @Schema(description = "메모(선택)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 500, message = "memo는 500자를 초과할 수 없습니다")
    val memo: String? = null,

    @Schema(description = "제출 시간 조정(분). 양수면 미래 시간, 음수면 과거 시간으로 설정. null이면 현재 시간 사용", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Min(-1440, message = "제출 시간은 최대 24시간 전까지만 조정 가능합니다")
    @field:Max(0, message = "제출 시간은 미래로 설정할 수 없습니다")
    val submittedAtAdjustMinutes: Int? = null,

    @Schema(description = "제출 파일 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Valid
    val files: List<AssignmentFileCreateReq> = emptyList()
)
