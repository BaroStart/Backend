package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.QueryUseCase
import java.time.Duration
import java.time.LocalTime

@QueryUseCase
class ToDoQueryUseCase(
    private val toDoRepository: ToDoRepository
) {

    fun is7DaysToDoCompletedStreak(menteeId: Long): Boolean =
        toDoRepository.findMaxConsecutivePerfectDays(menteeId) >= 7

    fun getCompletedOver25MinutesCount(menteeId: Long): Long =
        toDoRepository.countCompletedOver25Minutes(menteeId)

    fun getStudyBetweenSixAndNineCount(menteeId: Long): Long =
        toDoRepository.countStudyBetweenSixAndNine(menteeId)
}
