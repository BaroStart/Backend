package com.barostartbe.domain.todo.controller

import com.barostartbe.domain.todo.dto.request.ChangeToDoStatusReq
import com.barostartbe.domain.todo.dto.request.CreateToDoReq
import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.dto.response.ToDoRes
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/todos")
@Tag(name = "ToDo API", description = "할 일 관리 API")
interface ToDoApi {

    @GetMapping
    @Operation(summary = "오늘의 할 일 목록 조회", description = "오늘의 모든 할 일을 조회합니다.")
    fun getTodayToDoList(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<ToDoRes>>>

    @PostMapping
    @Operation(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.")
    fun createToDo(
        @AuthenticationPrincipal user: User,
        @RequestBody createToDoReq: CreateToDoReq
    ): ResponseEntity<ApiResponse<Unit>>

    @PutMapping
    @Operation(summary = "할 일 수정", description = "기존 할 일을 수정합니다.")
    fun updateToDo(
        @RequestBody updateToDoReq: UpdateToDoReq
    ): ResponseEntity<ApiResponse<Unit>>

    @PatchMapping("/{id}/status")
    @Operation(summary = "할 일 상태 변경", description = "할 일의 완료/미완료 상태를 변경합니다.")
    fun changeToDoStatus(
        @RequestBody changeToDoStatusReq: ChangeToDoStatusReq
    ): ResponseEntity<ApiResponse<Unit>>

    @DeleteMapping("/{id}")
    @Operation(summary = "할 일 삭제", description = "ID로 특정 할 일을 삭제합니다.")
    fun deleteToDo(
        @Parameter(description = "삭제할 할 일 ID", required = true, example = "1") @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>>
}
