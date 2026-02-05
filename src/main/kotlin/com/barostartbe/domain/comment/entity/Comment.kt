package com.barostartbe.domain.comment.entity

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "comments")
class Comment (
    @ManyToOne
    @JoinColumn(name = "mentee_id")
    var mentee: Mentee? = null,

    var content: String? = null
): BaseEntity(){
}