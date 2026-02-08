package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.assignment.entity.Assignment
import java.time.LocalDateTime

data class GetMenteeFeedbackDashboardResponseDto(
    val assignmentId: Long,
    val assignmentTitle: String,
    val assignmentStatus: String,
    val submittedAt: LocalDateTime,
    val subject: String,
){
    companion object{
        fun from(assignment: Assignment): GetMenteeFeedbackDashboardResponseDto{
            return GetMenteeFeedbackDashboardResponseDto(
                assignmentId = assignment.id!!,
                assignmentTitle = assignment.title,
                assignmentStatus = assignment.status.name,
                submittedAt = assignment.submittedAt!!,
                subject = assignment.subject.name
            )
        }
    }
}
