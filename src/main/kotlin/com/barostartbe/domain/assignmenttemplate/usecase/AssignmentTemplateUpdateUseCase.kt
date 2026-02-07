package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateUpdateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileRes
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.global.error.exception.ServiceException

import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.annotation.CommandUseCase

@CommandUseCase
class AssignmentTemplateUpdateUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
) {

    fun execute(
        mentor: Mentor,
        templateId: Long,
        req: AssignmentTemplateUpdateReq
    ): AssignmentTemplateDetailRes {

        // 템플릿 조회
        val template = assignmentTemplateRepository.findById(templateId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_NOT_FOUND) }

        if (template.mentor.id != mentor.id) {
            throw ServiceException(ErrorCode.ASSIGNMENT_TEMPLATE_PERMISSION_DENIED)
        }

        // 텍스트 필드 수정
        template.update(
            name = req.name,
            description = req.description,
            title = req.title,
            content = req.content
        )

        // 기존 파일 전부 삭제
        req.files?.let { newFiles ->
            assignmentTemplateFileRepository.deleteAllByAssignmentTemplate(template)

            val entities = newFiles.map {
                AssignmentTemplateFile(
                    assignmentTemplate = template,
                    fileName = it.fileName,
                    url = it.url
                )
            }
            assignmentTemplateFileRepository.saveAll(entities)
        }

        // 최신 파일 목록 조회
        val currentFiles = assignmentTemplateFileRepository
            .findAllByAssignmentTemplate(template)

        // 상세 DTO 반환
        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description,
            title = template.title,
            content = template.content,
            files = currentFiles.map {
                AssignmentTemplateFileRes(
                    fileName = requireNotNull(it.fileName),
                    url = requireNotNull(it.url)
                )
            }
        )
    }
}
