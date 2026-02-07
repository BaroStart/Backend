package com.barostartbe.domain.assignment.entity

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "assignment_goals")
class AssignmentGoal(

    // 목표 이름
    @Column(name = "name", nullable = false, length = 50)
    val name: String,

    // 과목
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subject: Subject

) : BaseEntity()
