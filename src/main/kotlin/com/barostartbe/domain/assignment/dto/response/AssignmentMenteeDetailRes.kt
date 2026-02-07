package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "[멘티] 과제 상세 조회 응답")
data class AssignmentMenteeDetailRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "과제 제목")
    val title: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "마감 일시")
    val dueDate: LocalDateTime,

    @Schema(description = "과제 목표")
    val goal: String?,

    @Schema(description = "과제 내용")
    val content: String?,

    @Schema(description = "학습자료 목록")
    val materials: List<AssignmentFileRes>,

    @Schema(description = "제출 완료 시간")
    val submittedAt: LocalDateTime?,

    @Schema(description = "멘티 메모 (제출 시 작성)")
    val memo: String?,

    @Schema(description = "제출 파일 목록")
    val submissions: List<AssignmentFileRes>
)