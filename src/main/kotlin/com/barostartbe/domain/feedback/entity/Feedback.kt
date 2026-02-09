package com.barostartbe.domain.feedback.entity

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "feedbacks", uniqueConstraints = [UniqueConstraint(columnNames = ["assignment_id"])])
class Feedback(

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false, updatable = false)
    val assignment: Assignment, // 피드백은 반드시 과제(제출)에 종속

    @Lob
    @Column(nullable = false)
    var content: String,        // 멘토가 작성한 피드백 내용

    @Column(columnDefinition = "TEXT")
    var summary: String?        // 피드백 요약

) : BaseEntity() {

    companion object {

        fun create(
            assignment: Assignment,
            content: String,
            summary: String?
        ): Feedback {
            return Feedback(
                assignment = assignment,
                content = content,
                summary = summary
            )
        }
    }

    // 피드백 본문 수정
    fun updateContent(content: String) {
        this.content = content
    }

    // 피드백 요약 수정
    fun updateSummary(summary: String?) {
        this.summary = summary
    }
}
