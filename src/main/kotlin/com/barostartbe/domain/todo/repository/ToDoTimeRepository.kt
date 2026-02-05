package com.barostartbe.domain.todo.repository

import com.barostartbe.domain.todo.entity.ToDoTime
import org.springframework.data.jpa.repository.JpaRepository

interface ToDoTimeRepository : JpaRepository<ToDoTime, Long> {

    fun findByToDo_Id(toDoId: Long): List<ToDoTime>
    fun deleteByToDo_Id(toDoId: Long)
}
