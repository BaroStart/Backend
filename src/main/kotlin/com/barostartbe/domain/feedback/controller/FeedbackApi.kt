package com.barostartbe.domain.feedback.controller

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.dto.request.FeedbackCreateReq
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackRes
import com.barostartbe.domain.feedback.dto.response.FeedbackListItemRes
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackSummaryRes
import com.barostartbe.domain.feedback.dto.response.MenteeFeedbackDetailRes
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/v1/feedbacks")
@Tag(name = "Feedback API", description = "피드백 관리 API")
interface FeedbackApi {

    // [멘토] 피드백 목록 조회
    @GetMapping
    @Operation(summary = "피드백 목록 조회", description = "멘토 기준으로 제출된 과제의 피드백 목록을 조회합니다.")
    fun getListByMentor(
        @AuthenticationPrincipal mentor: User): ResponseEntity<ApiResponse<List<FeedbackListItemRes>>>

    // [멘토] 피드백 생성
    @PostMapping("/{assignmentId}")
    @Operation(
        summary = "피드백 생성",
        description = "멘토가 제출된 과제에 대해 피드백을 작성합니다."
    )
    fun createFeedback(
        @AuthenticationPrincipal mentor: User,
        @Parameter(description = "과제 ID", required = true)
        @PathVariable assignmentId: Long,
        @Valid @RequestBody req: FeedbackCreateReq
    ): ResponseEntity<ApiResponse<Long>>

    // [멘티] 데일리 피드백 요약 목록 조회
    @GetMapping("/daily/summary")
    @Operation(
        summary = "오늘의 피드백 요약 조회",
        description = "멘티 기준 오늘 받은 피드백 요약 목록을 조회합니다."
    )
    fun getDailyFeedbackSummaries(@AuthenticationPrincipal mentee: User):
            ResponseEntity<ApiResponse<List<DailyFeedbackSummaryRes>>>

    // [멘티] 데일리 피드백 목록 조회
    @GetMapping("/daily")
    @Operation(
        summary = "데일리 피드백 목록 조회",
        description = "멘티 기준 오늘 받은 피드백 목록을 조회합니다. 과목 필터 가능"
    )
    fun getDailyFeedbacks(
        @AuthenticationPrincipal mentee: User,
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<DailyFeedbackRes>>>


    // [멘티] 과제 기준 피드백 상세 조회
    @GetMapping("/{assignmentId}")
    @Operation(
        summary = "멘티 피드백 상세 조회",
        description = "멘티가 자신의 과제에 대한 피드백 상세 내용을 조회합니다."
    )
    fun getMenteeFeedbackDetail(
        @AuthenticationPrincipal mentee: User,
        @Parameter(description = "과제 ID", required = true)
        @PathVariable assignmentId: Long
    ): ResponseEntity<ApiResponse<MenteeFeedbackDetailRes>>
}
