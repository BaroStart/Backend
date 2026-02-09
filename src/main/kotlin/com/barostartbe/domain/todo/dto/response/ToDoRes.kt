package com.barostartbe.domain.todo.dto.response

import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "할 일 응답 DTO")
data class ToDoRes(

    @Schema(description = "할 일 식별자", example = "1")
    val id: Long,

    @Schema(description = "할 일 제목", example = "국어 문제 풀기")
    val title: String,

    @Schema(description = "상태", example = "COMPLETED")
    val status: Status,

    @Schema(description = "시작 시간", example = "2023-10-01T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val startTime: LocalDateTime?,

    @Schema(description = "종료 시간", example = "2023-10-01T11:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val endTime: LocalDateTime?,
) {

    companion object {
        fun from(entity: ToDo): ToDoRes {
            return ToDoRes(
                id = entity.id!!,
                title = entity.title,
                status = entity.status,
                startTime = entity.startTime,
                endTime = entity.endTime
            )
        }
    }
}


