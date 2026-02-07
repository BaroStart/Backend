package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.annotation.QueryUseCase
import java.time.Duration

@QueryUseCase
class ToDoQueryUseCase(
    private val toDoRepository: ToDoRepository,
    private val toDoTimeRepository: ToDoTimeRepository
) {

    fun is7DaysToDoCompletedStreak(menteeId: Long): Boolean =
        toDoRepository.findMaxConsecutivePerfectDays(menteeId) >= 7

    fun getCompletedOver25MinutesCount(menteeId: Long): Long {
        val completedToDos = toDoRepository.findAllByMentee_IdAndStatus(menteeId, Status.COMPLETED)
        
        return completedToDos.count { todo ->
            val totalMinutes = toDoTimeRepository.findByToDo_Id(todo.id!!)
                .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
            totalMinutes >= 20
        }.toLong()
    }
}
