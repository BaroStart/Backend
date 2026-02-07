package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "[멘티] 과제 목록 조회 응답 DTO")
data class AssignmentMenteeListRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "과제명")
    val title: String,

    @Schema(description = "제출 여부")
    val status: AssignmentStatus,

    @Schema(description = "과제 목표")
    val goal: String?,

    @Schema(description = "제출 완료 시간 (미제출 시 null)")
    val submittedAt: LocalDateTime?
) {
    companion object {
        fun from(assignment: Assignment): AssignmentMenteeListRes =
            AssignmentMenteeListRes(
                assignmentId = assignment.id!!,
                subject = assignment.subject,
                title = assignment.title,
                status = assignment.status,
                goal = assignment.goal,
                submittedAt = assignment.submittedAt
            )
    }
}
