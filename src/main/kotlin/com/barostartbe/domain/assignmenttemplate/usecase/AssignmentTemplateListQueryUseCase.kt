package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateListRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AssignmentTemplateListQueryUseCase(
    private val assignmentTemplateRepository: AssignmentTemplateRepository,
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
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

        // 템플릿에 속한 파일 한 번에 조회
        val files = assignmentTemplateFileRepository
            .findAllByAssignmentTemplateIn(templates)

        // 템플릿 ID 기준으로 파일 그룹핑
        val fileMap = files.groupBy {
            requireNotNull(it.assignmentTemplate.id)
        }

        return templates.map { template ->
            AssignmentTemplateListRes(
                subject = template.subject,
                name = template.name,
                description = template.description,
                fileNames = fileMap[template.id]
                    ?.map { requireNotNull(it.fileName) }
                    ?: emptyList()
            )
        }
    }
}
