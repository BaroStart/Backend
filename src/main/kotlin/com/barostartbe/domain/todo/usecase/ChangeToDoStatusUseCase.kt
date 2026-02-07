package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.UpdateToDoStatusReq
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class ChangeToDoStatusUseCase(
    val toDoRepository: ToDoRepository,
    val toDoTimeRepository: ToDoTimeRepository
) {

    fun execute(updateToDoStatusReq: UpdateToDoStatusReq) {
        val entity = toDoRepository.findByIdOrNull(updateToDoStatusReq.id) ?: throw ToDoNotFoundException()
        entity.updateStatus(updateToDoStatusReq)

        // 기존 ToDoTime 삭제
        toDoTimeRepository.deleteAllByToDo_Id(entity.id!!)

        // 새로운 ToDoTime 목록 생성 및 저장
        updateToDoStatusReq.timeList?.let { timeList ->
            val toDoTimes = timeList.map { ToDoTime.of(it.startTime, it.endTime, entity) }
            toDoTimeRepository.saveAll(toDoTimes)
        }

        toDoRepository.save(entity)
    }
}
