package com.barostartbe.domain.example.controller

import com.barostartbe.domain.example.dto.response.ExampleRes
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v1/examples")
@Tag(name = "Example API", description = "예제 데이터 관리 API")
interface ExampleApi {

    // 예제 데이터 목록을 조회합니다
    @GetMapping
    @Operation(summary = "예제 데이터 목록 조회", description = "모든 예제 데이터를 조회합니다.")
    fun getAll(): ResponseEntity<ApiResponse<MutableList<ExampleRes>>>

    // ID로 특정 예제 데이터를 조회합니다
    @GetMapping("/{id}")
    @Operation(summary = "예제 데이터 단건 조회", description = "ID로 특정 예제 데이터를 조회합니다.")
    fun getById(
        @Parameter(description = "조회할 예제 ID", required = true, example = "1") @PathVariable id: Long?
    ): ResponseEntity<ApiResponse<ExampleRes>>
}
