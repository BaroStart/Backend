package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.todo.entity.ToDo
import java.time.LocalDateTime

data class GetMenteeTodoDashboardResponseDto(
    val title: String,
    val startAt: LocalDateTime,
    val finishAt: LocalDateTime,
    val status: String
){
    companion object{
        fun from(todo: ToDo): GetMenteeTodoDashboardResponseDto{
            return GetMenteeTodoDashboardResponseDto(
                title = todo.title,
                startAt = todo.startTime!!,
                finishAt = todo.endTime!!,
                status = todo.status.name
            )
        }
    }
}