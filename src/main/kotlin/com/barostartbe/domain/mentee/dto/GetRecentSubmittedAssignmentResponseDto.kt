package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.assignment.entity.Assignment
import java.time.LocalDateTime

data class GetRecentSubmittedAssignmentResponseDto(
    val assignmentId: Long,
    val title: String,
    val submittedAt: LocalDateTime,
    val dueDate: LocalDateTime
){
    companion object {
        fun from(assignment: Assignment): GetRecentSubmittedAssignmentResponseDto {
            return GetRecentSubmittedAssignmentResponseDto(
                assignmentId = assignment.id!!,
                title = assignment.title,
                submittedAt = assignment.submittedAt!!,
                dueDate = assignment.dueDate
            )
        }
    }
}
