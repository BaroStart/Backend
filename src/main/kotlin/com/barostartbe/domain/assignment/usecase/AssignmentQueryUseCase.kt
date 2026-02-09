package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.response.AssignmentFileRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeDetailRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeListRes
import com.barostartbe.domain.assignment.entity.enums.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDate

@QueryUseCase
class AssignmentQueryUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentFileRepository: AssignmentFileRepository
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
            .map { assignment ->
                AssignmentMenteeListRes.from(assignment)
            }
            .toList()
    }

    // [멘토] 과제 상세 조회
    fun getAssignmentDetail(
        assignmentId: Long,
        mentorId: Long
    ): AssignmentMenteeDetailRes {

        val assignment = findAssignmentOrThrow(assignmentId)

        // 멘토 본인 과제만 조회 (권한 체크)
        if (assignment.mentor.id != mentorId) {
            throw ServiceException(ErrorCode.ASSIGNMENT_PERMISSION_DENIED)
        }

        val (materials, submissions) = loadMaterialAndSubmission(assignmentId)

        return AssignmentMenteeDetailRes(
            assignmentId = assignment.id!!,
            title = assignment.title,
            subject = assignment.subject,
            dueDate = assignment.dueDate,
            templateName = assignment.templateName,
            content = assignment.content,
            seolStudyContext = assignment.seolStudyContext,
            materials = materials,
            submittedAt = assignment.submittedAt,
            memo = assignment.memo,
            submissions = submissions
        )
    }

    // [멘티] 과제 상세 조회
    fun getAssignmentDetailByMentee(
        assignmentId: Long,
        menteeId: Long
    ): AssignmentMenteeDetailRes {

        val assignment = findAssignmentOrThrow(assignmentId)

        // 멘티 본인 과제만 조회
        if (assignment.mentee.id != menteeId) {
            throw ServiceException(ErrorCode.ASSIGNMENT_PERMISSION_DENIED)
        }

        // 학습 자료 (멘토, 멘티 업로드)
        val (materials, submissions) = loadMaterialAndSubmission(assignmentId)

        return AssignmentMenteeDetailRes(
            assignmentId = assignment.id!!,
            title = assignment.title,
            subject = assignment.subject,
            dueDate = assignment.dueDate,
            templateName = assignment.templateName,
            content = assignment.content,
            seolStudyContext = assignment.seolStudyContext,
            materials = materials,
            submittedAt = assignment.submittedAt,
            memo = assignment.memo,
            submissions = submissions
        )
    }


    fun checkCompletedAssignmentExists(menteeId: Long): Boolean =
        assignmentRepository.existsByMentee_IdAndStatusNot(menteeId, AssignmentStatus.NOT_SUBMIT)

    fun is7DaysAssignmentCompletedStreak(menteeId: Long): Boolean =
        assignmentRepository.findMaxConsecutivePerfectDays(menteeId) >= 7

    fun getTotalStudyTimeBySubject(menteeId: Long, subject: Subject? = null): Long =
        assignmentRepository.sumStudyTimeByMenteeId(menteeId, subject) ?: 0L

    fun getCompletedOver25MinutesCount(menteeId: Long): Long =
        assignmentRepository.countCompletedOver25Minutes(menteeId)

    fun getStudyBetweenSixAndNineCount(menteeId: Long): Long =
        assignmentRepository.countStudyBetweenSixAndNine(menteeId)


    // 과제 조회
    private fun findAssignmentOrThrow(assignmentId: Long) =
        assignmentRepository.findById(assignmentId)
            .orElseThrow { AssignmentNotFoundException() }

    // 파일 조회
    private fun loadMaterialAndSubmission(assignmentId: Long): Pair<List<AssignmentFileRes>, List<AssignmentFileRes>> {
        val materials = loadFiles(assignmentId, AssignmentFileType.MATERIAL)
        val submissions = loadFiles(assignmentId, AssignmentFileType.SUBMISSION)
        return materials to submissions
    }

    // 파일 조회 공통 로직
    private fun loadFiles(assignmentId: Long, fileType: AssignmentFileType): List<AssignmentFileRes> {
        return assignmentFileRepository
            .findAllByAssignmentIdAndFileType(
                assignmentId = assignmentId,
                fileType = fileType
            )
            .map { file ->
                AssignmentFileRes(
                    assignmentFileId = file.id!!,
                    fileType = file.fileType.name,
                    downloadUrl = file.url
                )
            }
    }
}

