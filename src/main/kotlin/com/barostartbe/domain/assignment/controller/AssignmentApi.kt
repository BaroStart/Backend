package com.barostartbe.domain.assignment.controller

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.dto.response.AssignmentDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentListRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMaterialRes
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignment API", description = "과제 관리 API")
interface AssignmentApi {

    // [멘토] 과제 생성
    @PostMapping
    @Operation(summary = "[멘토] 과제 생성", description = "멘토가 멘티에게 과제를 등록합니다.")
    fun createAssignment(
        menteeId: Long,
        req: AssignmentCreateReq
    ): ResponseEntity<ApiResponse<AssignmentCreateRes>>


    // [멘티] 과제 제출
    @PostMapping("/{assignmentId}/submit")
    @Operation(summary = "[멘티] 과제 제출", description = "멘티가 멘토에게 과제를 제출합니다.")
    fun submitAssignment(
        @PathVariable assignmentId: Long,
        @RequestBody @Valid req: AssignmentSubmitReq
    ): ResponseEntity<ApiResponse<Unit>>


    // 과제 파일 다운로드 URL 조회
    @GetMapping("/files/{assignmentFileId}/download")
    @Operation(summary = "과제 파일 다운로드 URL 조회", description = "과제에 첨부된 파일의 다운로드 URL을 발급합니다.")
    fun getAssignmentFileDownloadUrl(
        @PathVariable assignmentFileId: Long
    ): ResponseEntity<ApiResponse<String>>


    // 과제 목록 조회
    @GetMapping
    @Operation(summary = "과제 목록 조회", description = "로그인 사용자 기준으로 과제 목록을 조회합니다.")
    fun getAssignments(): ResponseEntity<ApiResponse<List<AssignmentListRes>>>


    // 과제 단건 조회
    @GetMapping("/{assignmentId}")
    @Operation(summary = "과제 단건 조회", description = "과제 상세 정보를 조회합니다.")
    fun getAssignmentDetail(
        @PathVariable assignmentId: Long
    ): ResponseEntity<ApiResponse<AssignmentDetailRes>>

    // [멘토] 학습자료 목록 조회
    @GetMapping("/materials")
    @Operation(summary = "[멘토] 학습자료 전체 조회", description = "멘토가 등록한 모든 학습자료를 조회합니다.")
    fun getAllMaterials(
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<AssignmentMaterialRes>>>
}
