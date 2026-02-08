package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileRes
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateCreateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.CommandUseCase

@CommandUseCase
class AssignmentTemplateCreateUseCase(

    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
) {

    fun execute(
        mentor: Mentor,
        req: AssignmentTemplateCreateReq
    ): AssignmentTemplateDetailRes {

        // 템플릿 생성
        val template: AssignmentTemplate = assignmentTemplateRepository.save(
            AssignmentTemplate(
                mentor = mentor,
                subject = req.subject,
                name = req.name,    // 목표명
                description = req.description,
                title = req.title,
                content = req.content
            )
        )

        // 템플릿 파일 생성
        val templateFiles = req.files.map {
            AssignmentTemplateFile(
                assignmentTemplate = template,
                fileName = it.fileName,
                url = it.url
            )
        }

        assignmentTemplateFileRepository.saveAll(templateFiles)


        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description,
            title = template.title,
            content = template.content,
            files = templateFiles.map {
                AssignmentTemplateFileRes(
                    fileName = requireNotNull(it.fileName),
                    url = requireNotNull(it.url)
                )
            }
        )
    }
}
