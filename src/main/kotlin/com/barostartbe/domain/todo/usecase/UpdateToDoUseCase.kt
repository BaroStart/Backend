package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.util.ToDoValidator
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class UpdateToDoUseCase(
    val toDoRepository: ToDoRepository,
    val toDoValidator: ToDoValidator
) {

    fun execute(updateToDoReq: UpdateToDoReq, user: User) {
        toDoValidator.validateTimeConflict(user.id!!, updateToDoReq.startTime!!, updateToDoReq.endTime!!)

        val entity = toDoRepository.findByIdOrNull(updateToDoReq.id) ?: throw ToDoNotFoundException()
        entity.updateTitle(updateToDoReq)

        toDoRepository.save(entity)
    }
}
