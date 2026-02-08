package com.barostartbe.domain.assignment.entity

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.error.AssignmentFeedbackedException
import com.barostartbe.domain.assignment.error.AssignmentNotSubmittedException
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assignments")
class Assignment(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    val mentor: Mentor,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    val mentee: Mentee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    val assignmentTemplate: AssignmentTemplate? = null,

    @Column(name = "goal_text", columnDefinition = "TEXT", nullable = false)
    val goalText: String,

    @Column(nullable = false, length = 100)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subject: Subject,                   // 국, 영, 수, 사, 과

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AssignmentStatus = AssignmentStatus.NOT_SUBMIT,

    @Column(name = "date", nullable = false)
    val dueDate: LocalDateTime,             // 마감일시

    @Column(columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "start_time")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time")
    var endTime: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    @Column(name = "submitted_at")
    var submittedAt: LocalDateTime? = null,

    @Column(name = "seol_study_context", columnDefinition = "TEXT")
    var seolStudyContext: String? = null

) : BaseEntity() {

    // [멘토] 피드백 완료 처리 (SUBMITTED 상태에서만 가능)
    fun markFeedbacked() {
        if (this.status != AssignmentStatus.SUBMITTED) {
            throw AssignmentNotSubmittedException()
        }
        this.status = AssignmentStatus.FEEDBACKED
    }

    // [멘티] 과제 제출 / 재제출
    fun submit(startTime: LocalDateTime?, endTime: LocalDateTime?, memo: String?, submittedAt: LocalDateTime? = null) {
        if (this.status == AssignmentStatus.FEEDBACKED) {
            throw AssignmentFeedbackedException()
        }

        // 시간 기반 제출
        this.startTime = startTime
        this.endTime = endTime
        this.memo = memo
        this.status = AssignmentStatus.SUBMITTED
        this.submittedAt = submittedAt ?: LocalDateTime.now()
    }

    // [멘티] 제출 취소
    fun cancelSubmission() {
        if (this.status == AssignmentStatus.FEEDBACKED) {
            throw AssignmentFeedbackedException()
        }

        this.status = AssignmentStatus.NOT_SUBMIT

        // 시간도 함께 롤백
        this.startTime = null
        this.endTime = null
        this.memo = null
        this.submittedAt = null
    }

    companion object {

        // [멘토] 과제 생성
        fun create(
            mentor: Mentor,
            mentee: Mentee,
            assignmentTemplate: AssignmentTemplate?,
            goalText: String,
            req: AssignmentCreateReq
        ): Assignment =
            Assignment(
                mentor = mentor,
                mentee = mentee,
                assignmentTemplate = assignmentTemplate,
                goalText = goalText,
                title = req.title,
                subject = req.subject,
                dueDate = req.dueDate,
                content = req.content,
                seolStudyContext = req.seolStudyColumn
            )
    }
}
