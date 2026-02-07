package com.barostartbe.domain.assignmenttemplate.controller

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileListRes
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/v1/assignment-template-files")
@Tag(name = "Assignment Template File API", description = "과제 템플릿 학습자료 조회 API (멘토 전용)")
interface AssignmentTemplateFileApi {

    @GetMapping
    @Operation(summary = "과제 템플릿 학습자료 목록 조회", description = "멘토가 생성한 과제 템플릿에 포함된 학습자료 파일 목록을 조회합니다. 과목 필터링이 가능합니다.")
    fun getTemplateFileList(
        @AuthenticationPrincipal mentor: Mentor,
        @Parameter(
            description = "과목 필터 (선택)",
            example = "MATH"
        )
        @RequestParam(required = false)
        subject: Subject?
    ): ResponseEntity<ApiResponse<List<AssignmentTemplateFileListRes>>>
}
