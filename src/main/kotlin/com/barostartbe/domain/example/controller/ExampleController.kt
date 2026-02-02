package com.barostartbe.domain.example.controller

import com.barostartbe.domain.example.dto.response.ExampleRes
import com.barostartbe.domain.example.usecase.ExampleQueryUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleController(
    private val exampleQueryUseCase: ExampleQueryUseCase
) : ExampleApi {

    override fun getAll(): ResponseEntity<ApiResponse<MutableList<ExampleRes>>> =
        ApiResponse.success(SuccessCode.CREATE_OK, exampleQueryUseCase.getAll())

    override fun getById(@PathVariable id: Long?): ResponseEntity<ApiResponse<ExampleRes>> =
        ApiResponse.success(SuccessCode.REQUEST_OK, exampleQueryUseCase.findById(id!!))
}
