package com.barostartbe.domain.assignment.dto.request

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "[멘토] 과제 생성 요청 DTO")
data class AssignmentCreateReq(

    // TODO: SecurityContext로 대체 예정
    @Schema(description = "과제 제목", example = "2월 15일 수학 미적분 학습", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "title은 필수입니다")
    @field:Size(max = 100, message = "title은 100자를 초과할 수 없습니다")
    val title: String,

    @Schema(description = "과목", example = "MATH", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "subject는 필수입니다")
    val subject: Subject,

    @Schema(description = "과제 날짜(마감일)", example = "2025-02-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "dueDate는 필수입니다")
    val dueDate: LocalDate,

    @Schema(description = "과제 목표(보완점/목표 텍스트)", example = "오답 원인 분석 및 재풀이", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 1000, message = "goal은 1000자를 초과할 수 없습니다")
    val goal: String? = null,

    @Schema(description = "과제 내용", example = "미적분 문제집 3단원 1~20번 풀이", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 3000, message = "content는 3000자를 초과할 수 없습니다")
    val content: String? = null,

    @Schema(description = "멘토가 첨부한 학습자료 fileId 목록")
    val fileUrls: List<String> = emptyList()
)
