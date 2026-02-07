package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class AssignmentTemplateDetailQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
) {

    fun execute(
        mentor: Mentor,
        templateId: Long
    ): AssignmentTemplateDetailRes {

        // 템플릿 조회
        val template = assignmentTemplateRepository.findByIdAndMentor(templateId, mentor)
            ?: throw ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_NOT_FOUND)

        // 템플릿에 속한 파일 전체 조회
        val files = assignmentTemplateFileRepository
            .findAllByAssignmentTemplate(template)

        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description,
            title = template.title,
            content = template.content,
            files = files.map {
                AssignmentTemplateFileRes(
                    fileName = requireNotNull(it.fileName),
                    url = requireNotNull(it.url)
                )
            }
        )
    }
}
