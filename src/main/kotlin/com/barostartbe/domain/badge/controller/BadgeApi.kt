package com.barostartbe.domain.badge.controller

import com.barostartbe.domain.badge.dto.response.BadgeInfoResponse
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Badge API", description = "뱃지 관련 API")
@RequestMapping("/api/v1/badges")
interface BadgeApi {

    @GetMapping
    @Operation(summary = "내 뱃지 목록 조회", description = "현재 사용자가 획득한 뱃지 정보를 포함하여 전체 뱃지 목록을 조회합니다.")
    fun getMyBadges(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<BadgeInfoResponse>>>
}
