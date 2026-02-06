package com.barostartbe.domain.todo.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "할 일 생성 요청 DTO")
data class CreateToDoReq(

    @Schema(description = "할 일 제목", example = "국어 문제 풀기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    val title: String,
)
