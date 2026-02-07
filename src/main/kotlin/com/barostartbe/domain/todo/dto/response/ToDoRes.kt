package com.barostartbe.domain.todo.dto.response

import com.barostartbe.domain.todo.dto.base.TimeSlot
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "할 일 응답 DTO")
data class ToDoRes(

    @Schema(description = "할 일 제목", example = "국어 문제 풀기")
    val title: String,

    @Schema(description = "상태", example = "COMPLETED")
    val status: Status,

    @Schema(description = "시간 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val timeList: List<TimeSlot>?
) {

    companion object {
        fun from(entity: ToDo, timeList: List<ToDoTime>): ToDoRes {
            return ToDoRes(
                title = entity.title,
                status = entity.status,
                timeList = timeList.map {
                    TimeSlot(
                        startTime = it.startTime,
                        endTime = it.endTime
                    )
                }
            )
        }
    }
}


