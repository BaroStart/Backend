package com.barostartbe.domain.assignmenttemplate.entity

import com.barostartbe.global.common.entity.BaseFileEntity
import jakarta.persistence.*

@Entity
@Table(name = "assignment_template_file")
class AssignmentTemplateFile(

    // 과제 템플릿
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_template_id", nullable = false)
    val assignmentTemplate: AssignmentTemplate,

    fileName: String,
    url: String

) : BaseFileEntity(
    fileName = fileName,
    url = url
)
