package com.barostartbe.domain.learningresource.entity

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

/**
 * 학습 자료 엔티티
 */
@Entity
@Table(name = "learning_resources")
class LearningResource(

    // 파일명
    @Column(nullable = false, length = 255)
    val fileName: String,

    // 과목
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subject: Subject,

    // 파일 URL
    @Column(nullable = false, length = 500)
    val fileUrl: String,

    // 파일 사이즈 (byte)
    @Column(nullable = false)
    val fileSize: Long,

    // 자료 생성 주체 (설스터디 / 멘토)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val creatorType: LearningResourceCreatorType,

    // 멘토 업로드인 경우만 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    val mentor: Mentor? = null

) : BaseEntity()
// 자료 생성 주체 타입
enum class LearningResourceCreatorType {
    SEOL_STUDY,
    MENTOR
}