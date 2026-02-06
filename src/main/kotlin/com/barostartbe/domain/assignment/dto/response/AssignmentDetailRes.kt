package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "과제 상세 조회 응답 DTO")
data class AssignmentDetailRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "멘토 ID")
    val mentorId: Long,

    @Schema(description = "멘티 ID")
    val menteeId: Long,

    @Schema(description = "제목")
    val title: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "상태")
    val status: AssignmentStatus,

    @Schema(description = "수행 날짜(마감일)")
    val dueDate: LocalDate,

    @Schema(description = "과제 목표")
    val goal: String?,

    @Schema(description = "과제 내용")
    val content: String?,

    @Schema(description = "학습 시간")
    val studyTime: Int,

    @Schema(description = "메모")
    val memo: String?,

    @Schema(description = "제출 시간")
    val submittedAt: LocalDateTime?,

    @Schema(description = "학습자료 목록(멘토)")
    val materials: List<AssignmentFileRes>,

    @Schema(description = "제출물 목록(멘티)")
    val submissions: List<AssignmentFileRes>
) {
    companion object {

        fun from(
            assignment: Assignment,
            materials: List<AssignmentFileRes>,
            submissions: List<AssignmentFileRes>
        ): AssignmentDetailRes =
            AssignmentDetailRes(
                assignmentId = assignment.id!!,
                mentorId = assignment.mentorId,
                menteeId = assignment.menteeId,
                title = assignment.title,
                subject = assignment.subject,
                status = assignment.status,
                dueDate = assignment.dueDate,
                goal = assignment.goal,
                content = assignment.content,
                studyTime = assignment.studyTime,
                memo = assignment.memo,
                submittedAt = assignment.submittedAt,
                materials = materials,
                submissions = submissions
            )
    }
}