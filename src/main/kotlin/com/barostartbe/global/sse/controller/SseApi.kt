package com.barostartbe.global.sse.controller

import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.sse.dto.response.SseConnectionStatsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RequestMapping("/api/sse")
@Tag(name = "SSE API", description = "실시간 알림 관리 API")
interface SseApi {

    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "SSE 구독", description = "실시간 알림을 구독합니다.")
    fun subscribe(
        @Parameter(description = "구독할 토픽 목록 (쉼표 구분)", example = "assignment,todo")
        @RequestParam(required = false) topics: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): SseEmitter

    @GetMapping("/status")
    @Operation(summary = "SSE 연결 상태 조회", description = "현재 활성화된 SSE 연결 통계를 조회합니다.")
    fun getStatus(): ResponseEntity<ApiResponse<SseConnectionStatsResponse>>
}
