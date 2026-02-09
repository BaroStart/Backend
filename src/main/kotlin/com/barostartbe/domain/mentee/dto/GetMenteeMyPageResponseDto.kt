package com.barostartbe.domain.mentee.dto

data class GetMenteeMyPageResponseDto(
    val name: String,
    val weeklyCompleteRate: Int,
    val weeklyTotalStudyTimeHour: Int,
    val weeklyTotalStudyTimeMinute: Int,
    val weeklyTotalCompletedAssignmentCount: Int,
    val weeklyTotalAssignmentCount: Int,
    val weeklyCompleteRateBySubject: List<GetWeeklyCompleteRateBySubjectResponseDto>,
    val totalBadgeCount: Int
)
