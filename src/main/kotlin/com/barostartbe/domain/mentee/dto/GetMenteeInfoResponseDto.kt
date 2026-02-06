package com.barostartbe.domain.mentee.dto

data class GetMenteeInfoResponseDto(
    val menteeName: String,
    val menteeGrade: String,
    val isActive: Int,
    val lastAccess: Int,
    val mentoringStartDate: String,
    val totalStudyTime: Int? = null,
    val assignmentAchieveRate: Int? = null,
    val averageScore: Float? = null
)
