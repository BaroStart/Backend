package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@CommandUseCase
class FeedbackTemplateDeleteUseCase(
    private val feedbackTemplateRepository: FeedbackTemplateRepository
) {

    // 피드백 템플릿 삭제

    fun delete(templateId: Long) : FeedbackTemplate {

        val template = feedbackTemplateRepository.findById(templateId)
            .orElseThrow { ServiceException(ErrorCode.FEEDBACK_TEMPLATE_NOT_FOUND) }

        if (template.usageCount > 0) {  // 사용 중인 템플릿 삭제 불가
            throw ServiceException(ErrorCode.FEEDBACK_TEMPLATE_DELETE_FORBIDDEN)
        }

        feedbackTemplateRepository.delete(template)
        return template
    }
}
