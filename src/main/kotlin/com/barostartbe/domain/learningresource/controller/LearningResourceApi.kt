package com.barostartbe.domain.learningresource.controller

import com.barostartbe.domain.learningresource.dto.request.LearningResourceCreateReq
import com.barostartbe.domain.learningresource.dto.response.LearningResourceListItemRes
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/v1/learning-resources")
@Tag(name = "Learning Resource API", description = "학습 자료 관리 API")
interface LearningResourceApi {

    @GetMapping
    @Operation(summary = "학습 자료 목록 조회", description = "로그인한 멘토 본인이 등록한 학습 자료 목록을 최신 등록순으로 조회합니다.")
    fun getList(@AuthenticationPrincipal mentor: Mentor
    ): ResponseEntity<ApiResponse<List<LearningResourceListItemRes>>>

    @PostMapping
    @Operation(summary = "학습 자료 등록", description = "멘토가 학습 자료를 등록합니다. 파일 업로드는 프론트에서 OCL PUT으로 처리합니다.")
    fun create(
        @AuthenticationPrincipal mentor: Mentor,
        @Valid @RequestBody req: LearningResourceCreateReq
    ): ResponseEntity<ApiResponse<LearningResourceListItemRes>>
}
