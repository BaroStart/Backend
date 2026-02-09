package com.barostartbe.global.sse.controller

import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import com.barostartbe.global.sse.dto.response.SseConnectionStatsResponse
import com.barostartbe.global.sse.usecase.SseQueryUseCase
import com.barostartbe.global.sse.usecase.SseSubscribeUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class SseController(
    private val sseSubscribeUseCase: SseSubscribeUseCase,
    private val sseQueryUseCase: SseQueryUseCase
) : SseApi {

    override fun subscribe(topics: String?, request: HttpServletRequest, response: HttpServletResponse): SseEmitter {
        response.setHeader("Cache-Control", "no-cache, no-transform")
        response.characterEncoding = "UTF-8"

        val sessionId = request.session.id
        return sseSubscribeUseCase.subscribe(sessionId, topics)
    }

    override fun getStatus(): ResponseEntity<ApiResponse<SseConnectionStatsResponse>> {
        return ApiResponse.success(SuccessCode.REQUEST_OK, sseQueryUseCase.getConnectionStats())
    }
}
