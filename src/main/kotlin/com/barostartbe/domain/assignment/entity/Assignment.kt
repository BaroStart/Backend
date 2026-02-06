package com.barostartbe.domain.assignment.entity

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentFeedbackedException
import com.barostartbe.domain.assignment.error.AssignmentNotSubmittedException

import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "assignments")
class Assignment(

    @Column(name = "mentor_id", nullable = false)
    val mentorId: Long,

    @Column(name = "mentee_id", nullable = false)
    val menteeId: Long,

    @Column(nullable = false, length = 100)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subject: Subject,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AssignmentStatus = AssignmentStatus.NOT_SUBMIT,

    @Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,

    @Column(columnDefinition = "TEXT")
    val goal: String? = null,

    @Column(columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "study_time")
    var studyTime: Int = 0,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    @Column(name = "submitted_at")
    var submittedAt: LocalDateTime? = null

) : BaseEntity() {

    // [멘토] 피드백 완료 처리 (SUBMITTED 상태에서만 가능)
    fun markFeedbacked() {
        if (this.status != AssignmentStatus.SUBMITTED) {
            throw AssignmentNotSubmittedException()
        }

        this.status = AssignmentStatus.FEEDBACKED
    }

    // [멘티] 과제 제출 / 재제출
    fun submit(studyTime: Int, memo: String?, submittedAt: LocalDateTime? = null) {
        if (this.status == AssignmentStatus.FEEDBACKED) {
            throw AssignmentFeedbackedException()
        }

        // SUBMITTED 상태에서도 재제출 허용
        this.studyTime = studyTime
        this.memo = memo
        this.status = AssignmentStatus.SUBMITTED
        this.submittedAt = submittedAt ?: LocalDateTime.now()   // null이면 현재 시간
    }

    // [멘티] 제출 취소
    fun cancelSubmission() {
        if (this.status == AssignmentStatus.FEEDBACKED) {
            throw AssignmentFeedbackedException()
        }

        this.status = AssignmentStatus.NOT_SUBMIT
        this.studyTime = 0
        this.memo = null
        this.submittedAt = null
    }

    companion object {
        // [멘토] 과제 생성
        fun create(
            mentorId: Long,
            menteeId: Long,
            req: AssignmentCreateReq
        ): Assignment =
            Assignment(
                mentorId = mentorId,
                menteeId = menteeId,
                title = req.title,
                subject = req.subject,
                dueDate = req.dueDate,
                goal = req.goal,
                content = req.content
            )
    }
}
