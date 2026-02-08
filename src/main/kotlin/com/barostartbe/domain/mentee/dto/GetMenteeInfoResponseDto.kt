package com.barostartbe.domain.mentee.dto

data class GetMenteeInfoResponseDto(
    val basicInfo: GetMenteeBasicInfoResponseDto,

    val todayAssignmentCount: Int,
    val todayCompletedAssignmentCount: Int,

    val weeklyAssignmentCount: Int,
    val weeklyCompletedAssignmentCount: Int,
    val weeklyCompletedAssignmentRate: Int,

    val waitFeedbackCount: Int,
    val recentSubmittedAssignment: List<GetRecentSubmittedAssignmentResponseDto>
)
