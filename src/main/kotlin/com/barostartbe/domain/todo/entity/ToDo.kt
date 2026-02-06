package com.barostartbe.domain.todo.entity

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.todo.dto.request.UpdateToDoStatusReq
import com.barostartbe.domain.todo.dto.request.CreateToDoReq
import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "todos")
class ToDo(

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status,

    @JoinColumn(name = "mentee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var mentee: Mentee? = null,
) : BaseEntity() {

    fun updateTitle(updateToDoReq: UpdateToDoReq) {
        title = updateToDoReq.title
    }

    fun updateStatus(updateToDoStatusReq: UpdateToDoStatusReq) {
        status = updateToDoStatusReq.status
    }

    companion object {
        fun of(mentee: Mentee, createToDoReq: CreateToDoReq): ToDo {
            return ToDo(
                mentee = mentee,
                title = createToDoReq.title,
                status = Status.NOT_COMPLETED,
            )
        }
    }
}
