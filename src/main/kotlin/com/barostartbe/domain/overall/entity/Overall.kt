package com.barostartbe.domain.overall.entity

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.overall.dto.request.OverallCreateRequest
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "overalls")
class Overall(

    @Column(nullable = false)
    val content: String,

    @JoinColumn(name = "mentee_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val mentee: Mentee,

    @JoinColumn(name = "mentor_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    val mentor: Mentor,
    ) : BaseEntity() {

    companion object {
        fun of(content : String, mentee: Mentee, mentor: Mentor): Overall {
            return Overall(
                content = content,
                mentee = mentee,
                mentor = mentor
            )
        }
    }
}
