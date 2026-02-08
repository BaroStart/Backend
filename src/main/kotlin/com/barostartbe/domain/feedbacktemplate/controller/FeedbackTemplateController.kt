package com.barostartbe.domain.feedbacktemplate.controller

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateCreateReq
import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateUpdateReq
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateListRes
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateRes
import com.barostartbe.domain.feedbacktemplate.usecase.FeedbackTemplateCreateUseCase
import com.barostartbe.domain.feedbacktemplate.usecase.FeedbackTemplateDeleteUseCase
import com.barostartbe.domain.feedbacktemplate.usecase.FeedbackTemplateQueryUseCase
import com.barostartbe.domain.feedbacktemplate.usecase.FeedbackTemplateUpdateUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedbackTemplateController(
    private val feedbackTemplateQueryUseCase: FeedbackTemplateQueryUseCase,
    private val feedbackTemplateCreateUseCase: FeedbackTemplateCreateUseCase,
    private val feedbackTemplateUpdateUseCase: FeedbackTemplateUpdateUseCase,
    private val feedbackTemplateDeleteUseCase: FeedbackTemplateDeleteUseCase
) : FeedbackTemplateApi {

    override fun getTemplates(subject: Subject?): ResponseEntity<ApiResponse<List<FeedbackTemplateListRes>>> =
        ApiResponse.success(SuccessCode.REQUEST_OK, feedbackTemplateQueryUseCase.getTemplates(subject))

    override fun getTemplate(templateId: Long): ResponseEntity<ApiResponse<FeedbackTemplateRes>> {
        val template = feedbackTemplateQueryUseCase.getTemplate(templateId)

        return ApiResponse.success(SuccessCode.REQUEST_OK, FeedbackTemplateRes.from(template))
    }

    override fun create(req: FeedbackTemplateCreateReq): ResponseEntity<ApiResponse<FeedbackTemplateRes>> {
        val template = feedbackTemplateCreateUseCase.create(req)

        return ApiResponse.success(SuccessCode.CREATE_OK, FeedbackTemplateRes.from(template))
    }

    override fun update(templateId: Long, req: FeedbackTemplateUpdateReq): ResponseEntity<ApiResponse<FeedbackTemplateRes>> {

        val updated = feedbackTemplateUpdateUseCase.update(templateId, req)

        return ApiResponse.success(SuccessCode.REQUEST_OK, FeedbackTemplateRes.from(updated))
    }

    override fun delete(templateId: Long): ResponseEntity<ApiResponse<FeedbackTemplateRes>> {
        val deleted = feedbackTemplateDeleteUseCase.delete(templateId)

        return ApiResponse.success(SuccessCode.REQUEST_OK, FeedbackTemplateRes.from(deleted))
    }
}
