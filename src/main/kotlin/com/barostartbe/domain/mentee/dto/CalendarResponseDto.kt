package com.barostartbe.domain.mentee.dto

import java.time.LocalDate

data class CalendarResponseDto(
    val date: LocalDate,
    val hasAssignment: Boolean,
    val hasToDo: Boolean
)
