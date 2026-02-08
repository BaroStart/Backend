package com.barostartbe.domain.assignment.dto.request

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "[멘토] 과제 생성 요청 DTO")
data class AssignmentCreateReq(
    @Schema(description = "대상 멘티 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "menteeId는 필수입니다")
    val menteeId: Long,

    @Schema(description = "과제 제목", example = "2월 15일 수학 미적분 학습", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "title은 필수입니다")
    @field:Size(max = 100, message = "title은 100자를 초과할 수 없습니다")
    val title: String,

    @Schema(description = "과목", example = "MATH", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "subject는 필수입니다")
    val subject: Subject,

    @Schema(description = "과제 마감 일시 (프론트에서 날짜별로 API 호출)", example = "2026-02-07T23:59", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotNull(message = "dueAt는 필수입니다")
    val dueDate: LocalDateTime,

    @Schema(description = "선택한 과제 템플릿 ID(선택 안하면 null)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val templateId: Long? = null,

    @Schema(description = "멘토가 직접 입력한 과제 목표", example = "오답 원인 분석 및 재풀이")
    @field:Size(max = 1000, message = "goalText는 1000자를 초과할 수 없습니다")
    val goalText: String? = null,

    @Schema(description = "과제 내용", example = "미적분 문제집 3단원 1~20번 풀이", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @field:Size(max = 3000, message = "content는 3000자를 초과할 수 없습니다")
    val content: String? = null,

    @Schema(description = "설스터디 칼럼 (멘토 코멘트/가이드)", example = "개념 이해 위주로 접근하고 풀이 과정 서술할 것")
    @field:Size(max = 2000, message = "column은 2000자를 초과할 수 없습니다")
    val seolStudyColumn: String? = null,


    @Schema(description = "멘토가 첨부한 학습자료 fileId 목록")
    val fileUrls: List<String> = emptyList()
)
