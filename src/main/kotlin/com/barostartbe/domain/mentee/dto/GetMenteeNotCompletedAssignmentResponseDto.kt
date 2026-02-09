package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.assignment.entity.Assignment
import java.time.LocalDateTime

data class GetMenteeNotCompletedAssignmentResponseDto(
    val assignmentId: Long,
    val title: String,
    val subject: String,
    val dueDate: LocalDateTime
){
    companion object {
        fun from(assignment: Assignment): GetMenteeNotCompletedAssignmentResponseDto {
            return GetMenteeNotCompletedAssignmentResponseDto(
                assignmentId = assignment.id!!,
                title = assignment.title,
                subject = assignment.status.name,
                dueDate = assignment.dueDate
            )
        }
    }
}
