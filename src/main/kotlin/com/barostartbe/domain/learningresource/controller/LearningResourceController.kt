package com.barostartbe.domain.learningresource.controller

import com.barostartbe.domain.learningresource.dto.request.LearningResourceCreateReq
import com.barostartbe.domain.learningresource.dto.response.LearningResourceListItemRes
import com.barostartbe.domain.learningresource.usecase.LearningResourceCreateUseCase
import com.barostartbe.domain.learningresource.usecase.LearningResourceListQueryUseCase
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LearningResourceController(
    private val learningResourceListQueryUseCase: LearningResourceListQueryUseCase,
    private val learningResourceCreateUseCase: LearningResourceCreateUseCase
) : LearningResourceApi {

    override fun getList(
        mentor: Mentor
    ): ResponseEntity<ApiResponse<List<LearningResourceListItemRes>>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            learningResourceListQueryUseCase.getList(mentor) // 멘토 기준 조회
        )

    override fun create(
        mentor: Mentor,
        req: LearningResourceCreateReq
    ): ResponseEntity<ApiResponse<LearningResourceListItemRes>> =
        ApiResponse.success(
            SuccessCode.CREATE_OK,
            learningResourceCreateUseCase.create(mentor, req)
        )
}
