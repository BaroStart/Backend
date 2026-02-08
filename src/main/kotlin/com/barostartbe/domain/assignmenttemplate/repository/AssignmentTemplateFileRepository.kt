package com.barostartbe.domain.assignmenttemplate.repository

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AssignmentTemplateFileRepository : JpaRepository<AssignmentTemplateFile, Long> {

    // 템플릿에 연결된 파일 전체 조회
    fun findAllByAssignmentTemplate(assignmentTemplate: AssignmentTemplate): List<AssignmentTemplateFile>

    // 템플릿 기준 파일 전체 삭제 (수정 시 replace 용도)
    fun deleteAllByAssignmentTemplate(assignmentTemplate: AssignmentTemplate)

    // 여러 템플릿에 속한 파일을 한 번에 조회
    fun findAllByAssignmentTemplateIn(templates: List<AssignmentTemplate>): List<AssignmentTemplateFile>

    // 멘토 기준 모든 템플릿의 파일 조회 (과목 무관, 최신순)
    @Query(
        """
        select f
        from AssignmentTemplateFile f
        join f.assignmentTemplate t
        where t.mentor = :mentor
        order by f.createdAt desc
        """
    )
    fun findAllByMentorOrderByCreatedAtDesc(
        @Param("mentor") mentor: Mentor
    ): List<AssignmentTemplateFile>

    // 과목 기준 파일 조회 (과목 필터링, 최신순)
    @Query(
        """
        select f
        from AssignmentTemplateFile f
        join f.assignmentTemplate t
        where t.mentor = :mentor
          and t.subject = :subject
        order by f.createdAt desc
        """
    )
    fun findAllByMentorAndSubjectOrderByCreatedAtDesc(
        @Param("mentor") mentor: Mentor,
        @Param("subject") subject: Subject
    ): List<AssignmentTemplateFile>

}
