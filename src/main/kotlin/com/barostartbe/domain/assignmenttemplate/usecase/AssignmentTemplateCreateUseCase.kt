package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.AssignmentGoal
import com.barostartbe.domain.assignment.repository.AssignmentGoalRepository
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
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository,
    private val assignmentGoalRepository: AssignmentGoalRepository
) {

    fun execute(
        mentor: Mentor,
        req: AssignmentTemplateCreateReq,
        files: List<AssignmentTemplateFileRes>
    ): AssignmentTemplateDetailRes {

        val assignmentGoal: AssignmentGoal = assignmentGoalRepository.save(
            AssignmentGoal(
                subject = req.subject,
                name = req.name
            )
        )

        // 템플릿 생성
        val template: AssignmentTemplate = assignmentTemplateRepository.save(
            AssignmentTemplate(
                mentor = mentor,
                subject = req.subject,
                assignmentGoal = assignmentGoal,
                name = req.name,
                description = req.description,
                title = req.title,
                content = req.content
            )
        )

        // 템플릿 파일 생성
        val templateFiles: List<AssignmentTemplateFile> = files.map {
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
