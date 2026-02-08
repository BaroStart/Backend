package com.barostartbe.domain.badge.repository

import com.barostartbe.domain.badge.entity.Badge
import org.springframework.data.jpa.repository.JpaRepository

interface BadgeRepository : JpaRepository<Badge, Long> {
}
