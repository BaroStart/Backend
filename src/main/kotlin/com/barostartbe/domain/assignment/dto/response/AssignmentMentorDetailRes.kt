package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "[멘토] 과제 상세 조회 응답")
data class AssignmentMentorDetailRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "과제 상태")
    val status: AssignmentStatus,

    @Schema(description = "과제 제목")
    val title: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "마감 일시")
    val dueDate: LocalDateTime,

    @Schema(description = "과제 템플릿 이름 (과제 목표)")
    val templateName: String,

    @Schema(description = "과제 내용")
    val content: String?,

    @Schema(description = "학습자료 목록")
    val materials: List<AssignmentFileRes>,

    @Schema(description = "설스터디 가이드")
    val seolStudyContext: String?,

    @Schema(description = "멘티 ID")
    val menteeId: Long,

    @Schema(description = "멘티 이름")
    val menteeName: String?,

    @Schema(description = "제출 완료 시간")
    val submittedAt: LocalDateTime?,

    @Schema(description = "멘티 메모 (제출 시 작성)")
    val memo: String?,

    @Schema(description = "제출 파일 목록")
    val submissions: List<AssignmentFileRes>
)
