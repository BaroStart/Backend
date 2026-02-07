package com.barostartbe.domain.assignment.entity

import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.global.common.entity.BaseFileEntity
import jakarta.persistence.*

@Entity
@Table(name = "assignment_file")
class AssignmentFile(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    val assignment: Assignment,

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    val fileType: AssignmentFileType,    // MATERIAL / SUBMISSION

    fileName: String,
    fileUrl: String,

    ) : BaseFileEntity(
    fileName = fileName,
    url = fileUrl
)