package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "과제 목록 조회 응답 DTO")
data class AssignmentListRes(
    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "제목")
    val title: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "상태")
    val status: AssignmentStatus,

    @Schema(description = "수행 날짜(마감일)")
    val dueDate: LocalDate,

    @Schema(description = "제출 시간")
    val submittedAt: LocalDateTime?
) {
    companion object {
        fun from(assignment: Assignment): AssignmentListRes =
            AssignmentListRes(
                assignmentId = assignment.id!!,
                title = assignment.title,
                subject = assignment.subject,
                status = assignment.status,
                dueDate = assignment.dueDate,
                submittedAt = assignment.submittedAt
            )
    }
}
