package com.barostartbe.domain.mentee.dto

data class GetMenteeDashboardResponseDto(
    val feedbacks: List<GetMenteeFeedbackDashboardResponseDto>,
    val notCompletedAssignments : List<GetMenteeNotCompletedAssignmentResponseDto>,
    val todos : List<GetMenteeTodoDashboardResponseDto>,
    val comments: List<GetMenteeCommentDashboardResponseDto>
)
