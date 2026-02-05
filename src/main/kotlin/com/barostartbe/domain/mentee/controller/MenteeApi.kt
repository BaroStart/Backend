package com.barostartbe.domain.mentee.controller

import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v1")
@Tag(name = "Mentee API", description = "멘티 정보를 crud 할 수 있는 api")
interface MenteeApi {

    @GetMapping("/mentee/{menteeId}")
    @Operation(summary = "멘티 정보 조회", description = "멘티 이름, 활동정보, 평균 점수 등 자세한 정보들을 조회하는 api")
    fun getMenteeInfo(@PathVariable menteeId: Long, @AuthenticationPrincipal mentor: User): ResponseEntity<ApiResponse<GetMenteeInfoResponseDto>>
}