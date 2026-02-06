package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.response.ToDoRes
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.annotation.QueryUseCase
import java.time.LocalDate

@QueryUseCase
class GetTodayToDoListUseCase(
    val toDoRepository: ToDoRepository,
    val toDoTimeRepository: ToDoTimeRepository
) {

    fun execute(menteeId: Long): List<ToDoRes> = toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now())
        .map {
            val timeList = toDoTimeRepository.findByToDo_Id(it.id!!)
            ToDoRes.from(it, timeList)
        }
        .toMutableList()
}
