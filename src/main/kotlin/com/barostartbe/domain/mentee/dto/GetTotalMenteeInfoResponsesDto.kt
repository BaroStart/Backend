package com.barostartbe.domain.mentee.dto

data class GetTotalMenteeInfoResponsesDto(
    val totalMentees: Int,
    val totalWaitFeedbackCount: Int,
    val todayTotalCompletedAssignmentCount: Int,
    val todayTotalAssignmentCount: Int,
    val weeklyCompleteAssignmentCount: Int
)
