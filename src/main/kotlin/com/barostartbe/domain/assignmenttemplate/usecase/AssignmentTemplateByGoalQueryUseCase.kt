package com.barostartbe.domain.assignmenttemplate.usecase


import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes.TemplateRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes.TemplateFileRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class AssignmentTemplateByGoalQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository,
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

        // 템플릿에 속한 파일 전체 조회
        val templateFiles = assignmentTemplateFileRepository
            .findAllByAssignmentTemplateIn(templates)

        val fileMap = templateFiles.groupBy {
            requireNotNull(it.assignmentTemplate.id)
        }


        val templateResponses = templates.map { template ->
            TemplateRes(
                templateId = template.id!!,
                name = template.name,
                description = template.description,
                title = template.title,
                content = template.content,
                files = fileMap[template.id]
                    ?.map {
                        TemplateFileRes(
                            id = it.id!!,
                            fileName = requireNotNull(it.fileName),
                            url = requireNotNull(it.url)
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
