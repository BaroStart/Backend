package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateLearningResourceRes
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateCreateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateLearningResource
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.learningresource.repository.LearningResourceRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@CommandUseCase
class AssignmentTemplateCreateUseCase(

    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val learningResourceRepository: LearningResourceRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository
) {
    fun execute(
        mentor: Mentor,
        req: AssignmentTemplateCreateReq
    ): AssignmentTemplateDetailRes {

        // 멘토 유효성 체크
        if (mentor.id == null) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 템플릿 생성
        val template: AssignmentTemplate = assignmentTemplateRepository.save(
            AssignmentTemplate(
                mentor = mentor,
                subject = req.subject,
                name = req.name,
                description = req.description,
                title = req.title,
                content = req.content
            )
        )

        // 학습 자료 Id 기반 조회 (멘토 본인 소유 학습 자료만)
        val learningResources =
            if (req.learningResourceIds.isEmpty()) {
                emptyList()
            } else {
                learningResourceRepository.findAllByMentorAndIdIn(
                    mentor = mentor,
                    ids = req.learningResourceIds
                )
            }

        if (learningResources.size != req.learningResourceIds.size) {
            throw ServiceException(ErrorCode.BAD_PARAMETER)
        }

        val relations = learningResources.map {
            AssignmentTemplateLearningResource(
                assignmentTemplate = template,
                learningResource = it
            )
        }

        assignmentTemplateLearningResourceRepository.saveAll(relations)


        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description ?: "",
            title = template.title,
            content = template.content ?: "",
            files = learningResources.map {
                AssignmentTemplateLearningResourceRes(
                    id = requireNotNull(it.id),
                    fileName = it.fileName,
                    url = it.fileUrl
                )
            }
        )
    }
}