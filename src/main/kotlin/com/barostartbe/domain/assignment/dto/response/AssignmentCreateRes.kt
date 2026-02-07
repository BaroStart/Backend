package com.barostartbe.domain.assignment.dto.response

import com.barostartbe.domain.assignment.entity.Assignment
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "[멘토] 과제 생성 응답 DTO")
data class AssignmentCreateRes(

    @Schema(description = "생성된 과제 ID")
    val assignmentId: Long
) {
    companion object {
        fun from(assignment: Assignment): AssignmentCreateRes {
            return AssignmentCreateRes(
                assignmentId = assignment.id!!
            )
        }
    }
}
