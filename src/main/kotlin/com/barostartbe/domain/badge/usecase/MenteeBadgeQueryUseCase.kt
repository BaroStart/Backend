package com.barostartbe.domain.badge.usecase

import com.barostartbe.domain.badge.dto.response.BadgeInfoResponse
import com.barostartbe.domain.badge.repository.BadgeRepository
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import com.barostartbe.global.annotation.QueryUseCase

@QueryUseCase
class MenteeBadgeQueryUseCase(
    private val menteeBadgeMappingRepository: MenteeBadgeMappingRepository,
    private val badgeRepository: BadgeRepository
) {

    fun getAllByMentee(menteeId: Long): List<BadgeInfoResponse> {
        val ownedBadgeIds = menteeBadgeMappingRepository.findAllByMentee_id(menteeId)
            .map { it.badge.id }
            .toSet()

        return badgeRepository.findAll().map { badge ->
            val isActive = badge.id in ownedBadgeIds
            BadgeInfoResponse.of(badge, isActive)
        }
    }
}
