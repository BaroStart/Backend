package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.dto.request.ChangeToDoStatusReq
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

    fun execute(changeToDoStatusReq: ChangeToDoStatusReq) {
        val entity = toDoRepository.findByIdOrNull(changeToDoStatusReq.id) ?: throw ToDoNotFoundException()
        entity.update(changeToDoStatusReq)

        // 기존 ToDoTime 삭제
        toDoTimeRepository.deleteByToDo_Id(entity.id!!)

        // 새로운 ToDoTime 목록 생성 및 저장
        changeToDoStatusReq.timeList?.let { timeList ->
            val toDoTimes = timeList.map { ToDoTime.of(it.startTime, it.endTime, entity) }
            toDoTimeRepository.saveAll(toDoTimes)
        }

        toDoRepository.save(entity)
    }
}
