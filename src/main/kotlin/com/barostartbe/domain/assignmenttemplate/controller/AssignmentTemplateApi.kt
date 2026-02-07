package com.barostartbe.domain.assignmenttemplate.controller

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateCreateReq
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateUpdateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateListRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileRes
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/assignment-templates")
@Tag(name = "Assignment Template API", description = "과제 템플릿 관리 API (멘토 전용)")
interface AssignmentTemplateApi {

    @PostMapping
    @Operation(summary = "과제 템플릿 생성", description = "멘토가 과제 템플릿을 생성합니다.")
    fun createTemplate(
        @AuthenticationPrincipal mentor: Mentor,
        @RequestBody req: AssignmentTemplateCreateReq,
        @RequestParam files: List<AssignmentTemplateFileRes>
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>>

    @PutMapping("/{id}")
    @Operation(summary = "과제 템플릿 수정", description = "멘토 본인이 생성한 과제 템플릿의 텍스트 및 첨부 파일을 수정합니다.")
    fun updateTemplate(
        @AuthenticationPrincipal mentor: Mentor,
        @Parameter(description = "과제 템플릿 ID") @PathVariable id: Long,
        @RequestBody req: AssignmentTemplateUpdateReq
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>>

    @GetMapping
    @Operation(summary = "과제 템플릿 목록 조회", description = "멘토 본인이 생성한 과제 템플릿 목록을 조회합니다. 과목 필터 가능")
    fun getTemplateList(
        @AuthenticationPrincipal mentor: Mentor,
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<AssignmentTemplateListRes>>>

    @GetMapping("/{id}")
    @Operation(summary = "과제 템플릿 상세 조회", description = "멘토 본인이 생성한 특정 과제 템플릿의 상세 정보를 조회합니다.")
    fun getTemplateDetail(
        @AuthenticationPrincipal mentor: Mentor,
        @Parameter(description = "과제 템플릿 ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>>

    @DeleteMapping("/{id}")
    @Operation(summary = "과제 템플릿 삭제", description = "멘토 본인이 생성한 과제 템플릿을 삭제합니다.")
    fun deleteTemplate(
        @AuthenticationPrincipal mentor: Mentor,
        @Parameter(description = "과제 템플릿 ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>>

}
