package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentFileRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeListRes
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDate

@QueryUseCase
class AssignmentQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {
    // [멘티] 과제 목록 조회
    fun getAssignmentsByMentee(
        menteeId: Long,
        subject: Subject? = null,
        dueDate: LocalDate? = null
    ): List<AssignmentMenteeListRes> {

        val assignments = assignmentRepository.findAllByMentee_Id(menteeId)

        return assignments
            .asSequence()
            .filter { subject == null || it.subject == subject }
            .filter { dueDate == null || it.dueDate.toLocalDate() == dueDate }
            .map { AssignmentMenteeListRes.from(it) }
            .toList()
    }


    // [멘티] 과제 상세 조회
    fun getAssignmentDetailByMentee(
        assignmentId: Long,
        menteeId: Long
    ): AssignmentMenteeDetailRes {

        val assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

        // 멘티 본인 과제만 조회
        if (assignment.mentee.id != menteeId) {
            throw ServiceException(ErrorCode.ASSIGNMENT_PERMISSION_DENIED)
        }

        // 학습 자료 (멘토 업로드)
        val materials = assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.MATERIAL
            )
            .map { file ->
                val downloadUrl = file.url

                AssignmentFileRes(
                    assignmentFileId = file.id!!,
                    fileType = file.fileType.name,
                    downloadUrl = downloadUrl
                )
            }

        // 제출물 (멘티 업로드)
        val submissions = assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = AssignmentFileType.SUBMISSION
            )
            .map { file ->
                val downloadUrl = file.url

                AssignmentFileRes(
                    assignmentFileId = file.id!!,
                    fileType = file.fileType.name,
                    downloadUrl = downloadUrl
                )
            }

        return AssignmentMenteeDetailRes(
            assignmentId = assignment.id!!,
            title = assignment.title,
            subject = assignment.subject,
            dueDate = assignment.dueDate,
            goal = assignment.goal,
            content = assignment.content,
            materials = materials,
            submittedAt = assignment.submittedAt,
            memo = assignment.memo,
            submissions = submissions
        )
    }

    fun getAssignmentDetail(assignmentId: Long) {}

    fun checkCompletedAssignmentExists(menteeId: Long): Boolean =
        assignmentRepository.existsByMentee_IdAndStatusNot(menteeId, AssignmentStatus.NOT_SUBMIT)

    fun is7DaysAssignmentCompletedStreak(menteeId: Long): Boolean =
        assignmentRepository.findMaxConsecutivePerfectDays(menteeId) >= 7

    // TODO: 과목별 및 누적 학습 시간 조회 기능 구현 (국어, 영어, 수학 50시간 이상, 전체 100시간)
    // TODO: 공부시간이 25분 넘는 과제가 25개가 넘는지 확인하는 기능 구현
    // TODO: 공부시간이 6~9시인 과제의 개수를 구하는 기능 구현 (endTime이 9시 이후인 경우 제외)
}
