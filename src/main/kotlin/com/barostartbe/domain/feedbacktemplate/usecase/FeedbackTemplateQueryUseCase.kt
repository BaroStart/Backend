package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateListRes
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class FeedbackTemplateQueryUseCase(
    private val feedbackTemplateRepository: FeedbackTemplateRepository
) {

    // 피드백 템플릿 목록 조회
    fun getTemplates(subject: Subject?): List<FeedbackTemplateListRes> {

        val templates = if (subject != null) {
            feedbackTemplateRepository.findAllBySubjectOrderByCreatedAtDesc(subject)
        } else {
            feedbackTemplateRepository.findAllByOrderByCreatedAtDesc()
        }

        return templates.map { FeedbackTemplateListRes.from(it) }
    }

    // 피드백 템플릿 상세 조회
    fun getTemplate(templateId: Long): FeedbackTemplate {
        return feedbackTemplateRepository.findById(templateId)
            .orElseThrow { ServiceException(ErrorCode.FEEDBACK_TEMPLATE_NOT_FOUND) }
    }
}
