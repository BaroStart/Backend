package com.barostartbe.domain.todo.entity

import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "todo_times")
class ToDoTime(

    @Column(nullable = false)
    var startTime: LocalDateTime,

    @Column(nullable = false)
    var endTime: LocalDateTime,

    @JoinColumn(name = "todo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var toDo: ToDo,
) : BaseEntity() {

    companion object {
        fun of(startTime: LocalDateTime, endTime: LocalDateTime, toDo: ToDo): ToDoTime {
            return ToDoTime(
                startTime = startTime,
                endTime = endTime,
                toDo = toDo,
            )
        }
    }
}
