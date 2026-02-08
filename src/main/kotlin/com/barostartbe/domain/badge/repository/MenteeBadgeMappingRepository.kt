package com.barostartbe.domain.badge.repository

import com.barostartbe.domain.badge.entity.MenteeBadgeMapping
import org.springframework.data.jpa.repository.JpaRepository

interface MenteeBadgeMappingRepository : JpaRepository<MenteeBadgeMapping, Long> {

    fun findAllByMentee_id(menteeId: Long): List<MenteeBadgeMapping>
}
