package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateUpdateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateLearningResourceRes
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateLearningResource
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.learningresource.repository.LearningResourceRepository
import com.barostartbe.global.error.exception.ServiceException

import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.annotation.CommandUseCase

@CommandUseCase
class AssignmentTemplateUpdateUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository,
    private val learningResourceRepository: LearningResourceRepository
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
        req.learningResourceIds?.let { newIds ->
            // 기존 관계 전부 삭제
            assignmentTemplateLearningResourceRepository
                .deleteAllByAssignmentTemplate(template)

            if (newIds.isNotEmpty()) {

                // 멘토 본인 소유 학습자료만 조회
                val learningResources =
                    learningResourceRepository.findAllByMentorAndIdIn(
                        mentor = mentor,
                        ids = newIds
                    )

                // 요청 ID 수와 실제 조회 수 불일치 시 에러
                if (learningResources.size != newIds.size) {
                    throw ServiceException(ErrorCode.BAD_PARAMETER)
                }

                // 템플릿-학습자료 관계 엔티티 생성
                val relations = learningResources.map {
                    AssignmentTemplateLearningResource(
                        assignmentTemplate = template,
                        learningResource = it
                    )
                }

                assignmentTemplateLearningResourceRepository.saveAll(relations)
            }
        }

        val currentRelations =
            assignmentTemplateLearningResourceRepository.findAllByAssignmentTemplate(template)

        // 상세 DTO 반환
        return AssignmentTemplateDetailRes(
            id = requireNotNull(template.id),
            subject = template.subject,
            name = template.name,
            description = template.description ?: "",
            title = template.title,
            content = template.content ?: "",
            files = currentRelations.map {
                AssignmentTemplateLearningResourceRes(
                    id = requireNotNull(it.learningResource.id),
                    fileName = it.learningResource.fileName,
                    url = it.learningResource.fileUrl
                )
            }
        )
    }
}
