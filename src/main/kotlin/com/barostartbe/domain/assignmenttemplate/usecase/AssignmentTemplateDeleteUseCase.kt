package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@CommandUseCase
class AssignmentTemplateDeleteUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository
) {

    fun execute(mentor: Mentor, templateId: Long) {

        val template = assignmentTemplateRepository.findById(templateId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_NOT_FOUND) }

        if (template.mentor.id != mentor.id) {
            throw ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_PERMISSION_DENIED)
        }

        // 템플릿-학습자료 관계 삭제
        assignmentTemplateLearningResourceRepository.deleteAllByAssignmentTemplate(template)

        // 템플릿 삭제
        assignmentTemplateRepository.delete(template)
    }
}
