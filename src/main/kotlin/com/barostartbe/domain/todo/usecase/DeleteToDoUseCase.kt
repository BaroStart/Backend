package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class DeleteToDoUseCase(
    val toDoRepository: ToDoRepository,
    val toDoTimeRepository: ToDoTimeRepository
) {

    fun execute(id: Long) {
        val entity = toDoRepository.findByIdOrNull(id) ?: throw ToDoNotFoundException()

        toDoTimeRepository.deleteByToDo_Id(entity.id!!)
        toDoRepository.delete(entity)
    }
}
