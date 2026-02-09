package com.barostartbe.domain.overall.controller

import com.barostartbe.domain.overall.dto.request.OverallCreateRequest
import com.barostartbe.domain.overall.dto.response.OverallResponse
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "Overall API", description = "총평 관련 API")
@RequestMapping("/api/v1/overalls")
interface OverallApi {

    @PostMapping
    @Operation(summary = "총평 등록", description = "멘토가 멘티에게 총평을 등록합니다.")
    fun createOverall(
        @AuthenticationPrincipal user: User,
        @RequestBody overallCreateRequest: OverallCreateRequest
    ): ResponseEntity<ApiResponse<Unit>>

    @GetMapping("/mentee")
    @Operation(summary = "내 총평 목록 조회", description = "멘티가 특정 날짜에 받은 모든 총평을 조회합니다.")
    fun getMyOveralls(
        @AuthenticationPrincipal user: User,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<ApiResponse<List<OverallResponse>>>

    @GetMapping("/mentor/{menteeId}")
    @Operation(summary = "작성한 총평 조회", description = "멘토가 특정 날짜에 특정 멘티에게 작성한 총평을 조회합니다.")
    fun getOverallByMentor(
        @AuthenticationPrincipal user: User,
        @PathVariable menteeId: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<ApiResponse<OverallResponse>>
}