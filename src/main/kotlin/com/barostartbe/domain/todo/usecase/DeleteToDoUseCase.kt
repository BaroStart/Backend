package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class DeleteToDoUseCase(
    val toDoRepository: ToDoRepository,
) {

    fun execute(id: Long) {
        val entity = toDoRepository.findByIdOrNull(id) ?: throw ToDoNotFoundException()
        toDoRepository.delete(entity)
    }
}
