package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedbacktemplate.dto.response.FeedbackTemplateSimpleRes
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class FeedbackTemplateSelectQueryUseCase(
    private val feedbackTemplateRepository: FeedbackTemplateRepository
) {

    fun getSelectableTemplates(subject: Subject): List<FeedbackTemplateSimpleRes> {

        return feedbackTemplateRepository
            .findAllBySubjectOrderByCreatedAtDesc(subject)
            .map {
                FeedbackTemplateSimpleRes(
                    id = it.id!!,
                    name = it.name,
                    preview = it.content
                )
            }
    }
}
