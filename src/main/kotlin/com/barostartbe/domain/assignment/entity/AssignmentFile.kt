package com.barostartbe.domain.assignment.entity

import com.barostartbe.domain.assignment.entity.enum.AssignmentFileUsage
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "assignment_file")
class AssignmentFile(

    @Column(name = "assignment_id", nullable = false)
    val assignmentId: Long,

    @Column(name = "file_id", nullable = false)
    val fileId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "file_usage",nullable = false)
    val usage: AssignmentFileUsage      // MATERIAL / SUBMISSION

) : BaseEntity()