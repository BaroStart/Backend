package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateLearningResourceRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class AssignmentTemplateDetailQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository
) {

    fun execute(
        mentor: Mentor,
        templateId: Long
    ): AssignmentTemplateDetailRes {

        // 템플릿 조회
        val template = assignmentTemplateRepository.findByIdAndMentor(templateId, mentor)
            ?: throw ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_NOT_FOUND)

        val relations = assignmentTemplateLearningResourceRepository
            .findAllByAssignmentTemplate(template)

        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description ?: "",
            title = template.title,
            content = template.content ?: "",
            files = relations.map {
                val resource = it.learningResource
                AssignmentTemplateLearningResourceRes(
                    id = requireNotNull(resource.id),
                    fileName = resource.fileName,
                    url = resource.fileUrl
                )
            }
        )
    }
}
