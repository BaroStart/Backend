package com.barostartbe.domain.assignmenttemplate.usecase


import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes.TemplateRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateLearningResourceRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class AssignmentTemplateByGoalQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateLearningResourceRepository: AssignmentTemplateLearningResourceRepository,
    private val mentorRepository: MentorRepository
) {

    // 목표명에 따른 템플릿 조회
    fun execute(mentorId: Long, subject: Subject, goalName: String): AssignmentTemplateByGoalRes {

        // 멘토 조회
        val mentor = mentorRepository.findById(mentorId)
            .orElseThrow { ServiceException(ErrorCode.USER_NOT_FOUND) }

        // 목표명에 해당하는 템플릿 조회
        val templates =
            assignmentTemplateRepository
                .findAllByMentorAndSubjectAndNameOrderByCreatedAtDesc(
                    mentor = mentor,
                    subject = subject,
                    name = goalName
                )

        if (templates.isEmpty()) {
            return AssignmentTemplateByGoalRes(
                goalName = goalName,
                subject = subject,
                templates = emptyList()
            )
        }

        // 템플릿에 속한 파일 한 번에 조회
        val relations =
            assignmentTemplateLearningResourceRepository
                .findAllByAssignmentTemplateIn(templates)

        val resourceMap = relations.groupBy {
            requireNotNull(it.assignmentTemplate.id)
        }

        val templateResponses = templates.map { template ->
            TemplateRes(
                templateId = requireNotNull(template.id),
                name = template.name,
                description = template.description ?: "",
                title = template.title,
                content = template.content ?: "",

                learningResources =
                    resourceMap[template.id]
                        ?.map { relation ->
                            val resource = relation.learningResource
                            AssignmentTemplateByGoalRes.TemplateLearningResourceRes(
                                id = requireNotNull(resource.id),
                                fileName = resource.fileName,
                                url = resource.fileUrl
                            )
                        }
                        ?: emptyList()
            )
        }


        return AssignmentTemplateByGoalRes(
            goalName = goalName,
            subject = subject,
            templates = templateResponses
        )
    }
}
