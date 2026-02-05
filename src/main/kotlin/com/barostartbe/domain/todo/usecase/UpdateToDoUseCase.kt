package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class UpdateToDoUseCase(
    val toDoRepository: ToDoRepository,
    val toDoTimeRepository: ToDoTimeRepository
) {

    fun execute(updateToDoReq: UpdateToDoReq) {
        // TODO: 다른 TODO와 과제랑 겹치는지 확인하는 로직 구축

        val entity = toDoRepository.findByIdOrNull(updateToDoReq.id) ?: throw ToDoNotFoundException()
        entity.update(updateToDoReq)

        if (entity.status == Status.COMPLETED) {
            // 기존 ToDoTime 삭제
            toDoTimeRepository.deleteByToDo_Id(entity.id!!)

            // 새로운 ToDoTime 목록 생성 및 저장
            updateToDoReq.timeList?.let { timeList ->
                val toDoTimes = timeList.map { ToDoTime.of(it.startTime, it.endTime, entity) }
                toDoTimeRepository.saveAll(toDoTimes)
            }
        } else {
            throw ServiceException(ErrorCode.TODO_NOT_COMPLETED)
        }

        toDoRepository.save(entity)
    }
}
