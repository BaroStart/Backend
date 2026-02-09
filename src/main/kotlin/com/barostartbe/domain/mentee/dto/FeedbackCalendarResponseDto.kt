package com.barostartbe.domain.mentee.dto

import java.time.LocalDate

data class FeedbackCalendarResponseDto(
    val date: LocalDate,
    val hasFeedback: Boolean
)
