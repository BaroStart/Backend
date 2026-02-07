package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileListRes
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class AssignmentTemplateFileListQueryUseCase(
    private val assignmentTemplateFileRepository: AssignmentTemplateFileRepository
) {

    /**
     * 과제 템플릿 학습자료 목록 조회
     * - 멘토가 소유한 과제 템플릿 학습자료 파일 전체 조회
     */
    fun execute(
        mentor: Mentor,
        subject: Subject?
    ): List<AssignmentTemplateFileListRes> {

        // 과목명 필터링 여부에 따른 파일 조회
        val files = if (subject == null) {
            assignmentTemplateFileRepository
                .findAllByMentorOrderByCreatedAtDesc(mentor)
        } else {
            assignmentTemplateFileRepository
                .findAllByMentorAndSubjectOrderByCreatedAtDesc(mentor, subject)
        }

        return files.map {
            AssignmentTemplateFileListRes(
                subject = it.assignmentTemplate.subject,
                fileName = requireNotNull(it.fileName),
                url = requireNotNull(it.url)
            )
        }
    }
}
