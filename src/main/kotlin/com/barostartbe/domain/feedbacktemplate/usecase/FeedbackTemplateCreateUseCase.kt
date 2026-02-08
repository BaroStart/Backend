package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateCreateReq
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.global.annotation.CommandUseCase

@CommandUseCase
class FeedbackTemplateCreateUseCase(
    private val feedbackTemplateRepository: FeedbackTemplateRepository
) {

    // 피드백 템플릿 생성
    fun create(req: FeedbackTemplateCreateReq) : FeedbackTemplate{

        val template = FeedbackTemplate(
            name = req.name,
            subject = req.subject,
            content = req.content
            // usageCount = 0 (기본값)
        )

        return feedbackTemplateRepository.save(template)
    }
}
