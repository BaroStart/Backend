package com.barostartbe.domain.feedbacktemplate.controller

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateCreateReq
import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateUpdateReq
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateListRes
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateRes
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateSimpleRes
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/v1/feedback-templates")
@Tag(name = "Feedback Template API", description = "피드백 템플릿 조회 API")
interface FeedbackTemplateApi {

    // 피드백 템플릿 목록 조회
    @GetMapping
    @Operation(summary = "피드백 템플릿 목록 조회", description = "과목별로 피드백 템플릿 목록을 조회합니다.")
    fun getTemplates(
        @Parameter(description = "과목 필터", required = false)
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<FeedbackTemplateListRes>>>

    // 피드백 템플릿 상세 조회
    @GetMapping("/{templateId}")
    @Operation(summary = "피드백 템플릿 상세 조회")
    fun getTemplate(
        @Parameter(description = "피드백 템플릿 ID", required = true)
        @PathVariable templateId: Long
    ): ResponseEntity<ApiResponse<FeedbackTemplateRes>>

    // 피드백 템플릿 생성
    @PostMapping
    @Operation(summary = "피드백 템플릿 생성")
    fun create(
        @Valid @RequestBody req: FeedbackTemplateCreateReq
    ): ResponseEntity<ApiResponse<FeedbackTemplateRes>>

    // 피드백 템플릿 수정
    @PutMapping("/{templateId}")
    @Operation(summary = "피드백 템플릿 수정")
    fun update(
        @Parameter(description = "피드백 템플릿 ID", required = true)
        @PathVariable templateId: Long,
        @Valid @RequestBody req: FeedbackTemplateUpdateReq
    ): ResponseEntity<ApiResponse<FeedbackTemplateRes>>

    // 피드백 템플릿 삭제
    @DeleteMapping("/{templateId}")
    @Operation(summary = "피드백 템플릿 삭제")
    fun delete(
        @Parameter(description = "피드백 템플릿 ID", required = true)
        @PathVariable templateId: Long
    ): ResponseEntity<ApiResponse<FeedbackTemplateRes>>


    // [피드백 작성용] 템플릿 선택 목록 조회
    @GetMapping("/select")
    @Operation(summary = "[피드백 생성] 피드백 템플릿 선택용 목록 조회")
    fun getSelectableTemplates(
        @RequestParam subject: Subject
    ): ResponseEntity<ApiResponse<List<FeedbackTemplateSimpleRes>>>
}
