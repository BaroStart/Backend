package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateListRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AssignmentTemplateListQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository
) {

    fun execute(
        mentor: Mentor,
        subject: Subject?
    ): List<AssignmentTemplateListRes> {

        // 멘토가 생성한 템플릿 조회 (과목별 필터링 가능)
        val templates = when (subject) {
            null -> assignmentTemplateRepository
                .findAllByMentorOrderByCreatedAtDesc(mentor)
            else -> assignmentTemplateRepository
                .findAllByMentorAndSubjectOrderByCreatedAtDesc(mentor, subject)
        }

        if (templates.isEmpty()) return emptyList()

        val relations = assignmentTemplateLearningResourceRepository
            .findAllByAssignmentTemplateIn(templates)

        val relationMap = relations.groupBy {
            requireNotNull(it.assignmentTemplate.id)
        }


        return templates.map { template ->
            AssignmentTemplateListRes(
                id = requireNotNull(template.id),
                subject = template.subject,
                name = template.name,
                description = template.description ?: "",
                fileNames =
                    relationMap[template.id]
                        ?.map {
                            it.learningResource.fileName
                        }
                        ?: emptyList()
            )
        }
    }
}
