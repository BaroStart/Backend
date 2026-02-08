package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class UpdateToDoUseCase(
    val toDoRepository: ToDoRepository
) {

    fun execute(updateToDoReq: UpdateToDoReq) {
        // TODO: 다른 TODO와 과제랑 겹치는지 확인하는 로직 구축

        val entity = toDoRepository.findByIdOrNull(updateToDoReq.id) ?: throw ToDoNotFoundException()
        entity.updateTitle(updateToDoReq)

        toDoRepository.save(entity)
    }
}
