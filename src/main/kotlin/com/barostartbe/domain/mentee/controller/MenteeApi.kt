package com.barostartbe.domain.mentee.controller

import com.barostartbe.domain.mentee.dto.CalendarResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.dto.TaskInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeBasicInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMentoMainDashboardResponseDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@RequestMapping("/api/v1")
@Tag(name = "Mentee API", description = "멘티 정보를 crud 할 수 있는 api")
interface MenteeApi {

    @GetMapping("/{menteeId}")
    @Operation(summary = "멘티 정보 조회", description = "멘티 이름, 활동정보, 평균 점수 등 자세한 정보들을 조회하는 api")
    fun getMenteeInfo(@PathVariable menteeId: Long, @AuthenticationPrincipal mentor: User): ResponseEntity<ApiResponse<GetMenteeBasicInfoResponseDto>>

    @GetMapping("/mentee/timetable")
    @Operation(summary = "멘티 타임 테이블 조회", description = "특정 날짜의 멘티의 타임테이블을 조회하는 api")
    fun getTimeTable(
        @AuthenticationPrincipal mentee: User,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<ApiResponse<List<TaskInfoResponseDto>>>

    @GetMapping("/mentee/calendar")
    @Operation(summary = "멘티 캘린더 조회", description = "특정 년월의 멘티의 캘린더 정보를 조회하는 api")
    fun getCalendar(
        @AuthenticationPrincipal mentee: User,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ApiResponse<List<CalendarResponseDto>>>

    @GetMapping("/mentee/{menteeId}/dashboard")
    @Operation(summary = "멘티 대시보드", description = "멘토 페이지에서 멘티 상세보기의 대시보드")
    fun getMenteeDetailsDashboard(
        @PathVariable menteeId: Long,
        @RequestParam searchType: String,
        @RequestParam date: String?,
        @AuthenticationPrincipal mentor: User
    ) : ResponseEntity<ApiResponse<GetMenteeDashboardResponseDto>>

    @GetMapping("/mentor/{mentorId}/dashboard")
    @Operation(summary = "맨토 대시보드", description = "멘토 페이지에서 멘토 대시보드")
    fun getMentorMainDashboard(@PathVariable mentorId: Long) : ResponseEntity<ApiResponse<GetMentoMainDashboardResponseDto>>
}