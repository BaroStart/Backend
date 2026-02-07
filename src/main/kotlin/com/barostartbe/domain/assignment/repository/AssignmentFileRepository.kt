package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.AssignmentFile
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AssignmentFileRepository : JpaRepository<AssignmentFile, Long> {

    // 특정 과제에 연결된 파일 목록 조회
    fun findAllByAssignmentId(assignmentId: Long): List<AssignmentFile>

    // 과제 파일의 용도(학습자료/과제 제출물)별 조회
    fun findAllByAssignmentIdAndFileType(assignmentId: Long, fileType: AssignmentFileType): List<AssignmentFile>

    /**
     * 제출 수정/재제출 시
     * 기존 SUBMISSION 매핑을 전부 삭제 처리
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        delete from AssignmentFile af
         where af.assignment.id = :assignmentId
           and af.fileType = :fileType
        """
    )
    fun deleteByAssignmentIdAndFileType(
        @Param("assignmentId") assignmentId: Long,
        @Param("fileType") fileType: AssignmentFileType
    )

    /**
     * 멘티가 제출물 삭제
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        delete from AssignmentFile af
         where af.assignment.id = :assignmentId
           and af.fileType = 'SUBMISSION'
        """
    )
    fun deleteAllSubmissionFiles(@Param("assignmentId") assignmentId: Long)
}