package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.todo.dto.request.CreateToDoReq
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class CreateToDoUseCase(
    val toDoRepository: ToDoRepository,
    val menteeRepository: MenteeRepository
) {

    fun execute(menteeId: Long, todoReqCreate: CreateToDoReq) {
        // TODO: 다른 TODO와 과제랑 겹치는지 확인하는 로직 구축

        val mentee = menteeRepository.findByIdOrNull(menteeId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)
        val entity = ToDo.of(mentee, todoReqCreate)
        toDoRepository.save(entity)
    }
}
