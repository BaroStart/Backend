package com.barostartbe.domain.badge.entity

import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "badges")
class Badge(

    @Column(nullable = false, length = 50, unique = true)
    val name: String,
    ) : BaseEntity() {
}
