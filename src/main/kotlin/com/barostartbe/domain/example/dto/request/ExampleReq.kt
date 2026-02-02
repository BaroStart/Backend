package com.barostartbe.domain.example.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "예제 생성 요청 DTO")
data class ExampleReq(

    @Schema(description = "예제 이름", example = "샘플 예제", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name은 필수입니다")
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    val name: String
)
