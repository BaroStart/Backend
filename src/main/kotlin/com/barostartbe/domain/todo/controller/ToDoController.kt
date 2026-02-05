package com.barostartbe.domain.todo.controller

import com.barostartbe.domain.todo.dto.request.ChangeToDoStatusReq
import com.barostartbe.domain.todo.dto.request.CreateToDoReq
import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.dto.response.ToDoRes
import com.barostartbe.domain.todo.usecase.*
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ToDoController(
    private val getTodayToDoListUseCase: GetTodayToDoListUseCase,
    private val createToDoUseCase: CreateToDoUseCase,
    private val updateToDoUseCase: UpdateToDoUseCase,
    private val changeToDoStatusUseCase: ChangeToDoStatusUseCase,
    private val deleteToDoUseCase: DeleteToDoUseCase
) : ToDoApi {

    override fun getTodayToDoList(@AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<List<ToDoRes>>> =
        ApiResponse.success(SuccessCode.REQUEST_OK, getTodayToDoListUseCase.execute(user.id!!))

    override fun createToDo(
        @AuthenticationPrincipal user: User,
        @RequestBody createToDoReq: CreateToDoReq
    ): ResponseEntity<ApiResponse<Unit>> {
        createToDoUseCase.execute(user.id!!, createToDoReq)
        return ApiResponse.success(SuccessCode.CREATE_OK)
    }

    override fun updateToDo(@RequestBody updateToDoReq: UpdateToDoReq): ResponseEntity<ApiResponse<Unit>> {
        updateToDoUseCase.execute(updateToDoReq)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun changeToDoStatus(@RequestBody changeToDoStatusReq: ChangeToDoStatusReq): ResponseEntity<ApiResponse<Unit>> {
        changeToDoStatusUseCase.execute(changeToDoStatusReq)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun deleteToDo(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        deleteToDoUseCase.execute(id)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }
}
