package com.barostartbe.domain.assignmenttemplate.entity

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "assignment_templates")
class AssignmentTemplate(

    // 템플릿 소유자 (멘토 개인 소유)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    val mentor: Mentor,

    // 과목 (목표/과제 필터링)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subject: Subject,

    // 템플릿 이름 (과제 목표명)
    @Column(nullable = false, length = 100)
    var name: String,

    // 템플릿 설명
    @Column(columnDefinition = "TEXT")
    var description: String,

    // 과제 이름
    @Column(nullable = false, length = 100)
    var title: String,

    // 과제 내용 (과제 생성 시 content로 복사)
    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

) : BaseEntity() {
    // 템플릿 수정
    fun update(
        name: String,
        description: String,
        title: String,
        content: String
    ) {
        this.name = name
        this.description = description
        this.title = title
        this.content = content
    }

}
