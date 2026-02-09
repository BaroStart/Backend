package com.barostartbe.domain.mentee.dto

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.todo.entity.ToDo
import java.time.LocalDateTime

data class TaskInfoResponseDto(
    val name: String,
    val type: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    companion object {
        fun createAssignment(assignment: Assignment): TaskInfoResponseDto {
            return TaskInfoResponseDto(
                name = assignment.title,
                type = "ASSIGNMENT",
                startTime = assignment.startTime!!,
                endTime = assignment.endTime!!
            )
        }

        fun createToDo(toDo: ToDo): TaskInfoResponseDto {
            return TaskInfoResponseDto(
                name = toDo.title,
                type = "TODO",
                startTime = toDo.startTime!!,
                endTime = toDo.endTime!!
            )
        }
    }
}
