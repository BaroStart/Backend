package com.barostartbe.domain.admin.entity

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "mentor_mentee_mappings")
class MentorMenteeMapping (
    @JoinColumn(name = "mentor_id")
    @ManyToOne
    val mentor: Mentor,

    @JoinColumn(name = "mentee_id")
    @ManyToOne
    val mentee: Mentee
): BaseEntity(){

}