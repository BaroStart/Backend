package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class ToDoQueryUseCase(
    private val toDoRepository: ToDoRepository
) {

    fun is7DaysToDoCompletedStreak(menteeId: Long): Boolean =
        toDoRepository.findMaxConsecutivePerfectDays(menteeId) >= 7
}
