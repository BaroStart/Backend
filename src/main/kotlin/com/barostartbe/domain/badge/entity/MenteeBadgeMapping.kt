package com.barostartbe.domain.badge.entity

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "mentee_badge")
class MenteeBadgeMapping(

    @JoinColumn(name = "mentee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val mentee: Mentee,

    @JoinColumn(name = "badge_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val badge: Badge
) : BaseEntity() {

    companion object {
        fun of(mentee: Mentee, badge: Badge): MenteeBadgeMapping {
            return MenteeBadgeMapping(
                mentee = mentee,
                badge = badge
            )
        }
    }
}
