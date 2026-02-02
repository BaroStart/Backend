package com.barostartbe.domain.example.dto.response

import com.barostartbe.domain.example.domain.Example
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "예제 응답 DTO")
data class ExampleRes(

    @Schema(description = "예제 이름", example = "샘플 예제")
    val name: String
) {
    companion object {
        fun from(example: Example): ExampleRes {
            return ExampleRes(
                name = example.name
            )
        }
    }
}
