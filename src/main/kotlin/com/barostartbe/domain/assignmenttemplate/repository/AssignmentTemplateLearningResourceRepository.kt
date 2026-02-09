package com.barostartbe.domain.assignmenttemplate.repository

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateLearningResource
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AssignmentTemplateLearningResourceRepository : JpaRepository<AssignmentTemplateLearningResource, Long> {

    // 템플릿에 연결된 파일 전체 조회
    fun findAllByAssignmentTemplate(assignmentTemplate: AssignmentTemplate): List<AssignmentTemplateLearningResource>

    // 템플릿 기준 파일 전체 삭제 (수정 시 replace 용도)
    fun deleteAllByAssignmentTemplate(assignmentTemplate: AssignmentTemplate)

    // 여러 템플릿에 속한 파일을 한 번에 조회
    fun findAllByAssignmentTemplateIn(templates: List<AssignmentTemplate>): List<AssignmentTemplateLearningResource>
}
