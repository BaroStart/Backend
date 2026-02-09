package com.barostartbe.domain.assignmenttemplate.entity

import com.barostartbe.global.common.entity.BaseEntity
import com.barostartbe.domain.learningresource.entity.LearningResource
import jakarta.persistence.*

@Entity
@Table(name = "assignment_template_file")
class AssignmentTemplateLearningResource(

    // 과제 템플릿
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_template_id", nullable = false)
    val assignmentTemplate: AssignmentTemplate,

    // 학습 자료
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_resource_id", nullable = false)
    val learningResource: LearningResource

) : BaseEntity()
