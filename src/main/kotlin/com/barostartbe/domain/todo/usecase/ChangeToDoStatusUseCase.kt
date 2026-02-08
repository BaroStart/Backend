package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.UpdateToDoStatusReq
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class ChangeToDoStatusUseCase(
    val toDoRepository: ToDoRepository,
) {

    fun execute(updateToDoStatusReq: UpdateToDoStatusReq) {
        val entity = toDoRepository.findByIdOrNull(updateToDoStatusReq.id) ?: throw ToDoNotFoundException()
        entity.updateStatus(updateToDoStatusReq)

        toDoRepository.save(entity)
    }
}
