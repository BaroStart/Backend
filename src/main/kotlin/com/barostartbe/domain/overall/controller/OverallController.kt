package com.barostartbe.domain.overall.controller

import com.barostartbe.domain.overall.dto.request.OverallCreateRequest
import com.barostartbe.domain.overall.dto.response.OverallResponse
import com.barostartbe.domain.overall.usecase.OverallCommandUseCase
import com.barostartbe.domain.overall.usecase.OverallQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class OverallController(
    private val overallCommandUseCase: OverallCommandUseCase,
    private val overallQueryUseCase: OverallQueryUseCase
) : OverallApi {

    override fun createOverall(user: User, overallCreateRequest: OverallCreateRequest): ResponseEntity<ApiResponse<Unit>> {
        overallCommandUseCase.create(overallCreateRequest, user.id!!)
        return ApiResponse.success(SuccessCode.CREATE_OK)
    }

    override fun getMyOveralls(user: User, date: LocalDate?): ResponseEntity<ApiResponse<List<OverallResponse>>> {
        val targetDate = date ?: LocalDate.now()
        val responses = overallQueryUseCase.getForMentee(user.id!!, targetDate)
        return ApiResponse.success(SuccessCode.REQUEST_OK, responses)
    }

    override fun getOverallByMentor(user: User, menteeId: Long, date: LocalDate?): ResponseEntity<ApiResponse<OverallResponse>> {
        val targetDate = date ?: LocalDate.now()
        val response = overallQueryUseCase.getForMentor(menteeId, user.id!!, targetDate)
        return ApiResponse.success(SuccessCode.REQUEST_OK, response)
    }
}