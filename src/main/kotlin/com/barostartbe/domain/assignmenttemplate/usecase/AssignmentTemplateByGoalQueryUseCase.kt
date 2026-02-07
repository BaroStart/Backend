package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.AssignmentGoal
import com.barostartbe.domain.assignment.repository.AssignmentGoalRepository
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes.TemplateRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateByGoalRes.TemplateFileRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

@QueryUseCase
class AssignmentTemplateByGoalQueryUseCase(
    private val assignmentGoalRepository: AssignmentGoalRepository,
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
) {

    fun execute(goalId: Long): AssignmentTemplateByGoalRes {

        // 목표 조회
        val goal: AssignmentGoal = assignmentGoalRepository.findById(goalId)
            .orElseThrow { ServiceException(ErrorCode.ASSIGNMENT_GOAL_NOT_FOUND) }

        // 목표에 속한 템플릿 조회
        val templates = assignmentTemplateRepository.findAllByAssignmentGoalOrderByCreatedAtDesc(goal)

        if (templates.isEmpty()) {
            return AssignmentTemplateByGoalRes(
                goalId = goal.id!!,
                goalName = goal.name,
                subject = goal.subject,
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
            goalId = goal.id!!,
            goalName = goal.name,
            subject = goal.subject,
            templates = templateResponses
        )
    }
}
