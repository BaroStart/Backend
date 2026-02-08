package com.barostartbe.domain.badge.dto.response

import com.barostartbe.domain.badge.entity.Badge

data class BadgeInfoResponse(
    val badgeId: Long,
    val name: String,
    val isActive: Boolean
) {
    companion object {
        fun of(badge: Badge, isActive: Boolean): BadgeInfoResponse {
            return BadgeInfoResponse(
                badgeId = badge.id!!,
                name = badge.name,
                isActive = isActive
            )
        }
    }
}
