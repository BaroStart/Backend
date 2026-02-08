package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import java.time.LocalDateTime

data class GetMenteeTodoDashboardResponseDto(
    val title: String,
    val startAt: LocalDateTime,
    val finishAt: LocalDateTime,
    val status: String
){
    companion object{
        fun of(todo: ToDo, timeSlot: ToDoTime): GetMenteeTodoDashboardResponseDto{
            return GetMenteeTodoDashboardResponseDto(
                title = todo.title,
                startAt = timeSlot.startTime,
                finishAt = timeSlot.endTime,
                status = todo.status.name
            )
        }
    }
}