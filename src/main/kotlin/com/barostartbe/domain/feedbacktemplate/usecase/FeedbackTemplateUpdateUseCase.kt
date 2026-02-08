package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateUpdateReq
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@CommandUseCase
class FeedbackTemplateUpdateUseCase(
    private val feedbackTemplateRepository: FeedbackTemplateRepository
) {

    // 피드백 템플릿 수정
    fun update(templateId: Long, req: FeedbackTemplateUpdateReq): FeedbackTemplate {

        val template = feedbackTemplateRepository.findById(templateId)
            .orElseThrow { ServiceException(ErrorCode.FEEDBACK_TEMPLATE_NOT_FOUND) }

        template.apply {
            name = req.name
            subject = req.subject
            content = req.content
        }

        return template
    }
}
