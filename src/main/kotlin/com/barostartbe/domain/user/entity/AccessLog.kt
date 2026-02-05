package com.barostartbe.domain.user.entity

import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "access_logs")
class AccessLog(
    @Column(nullable = false) val userId: Long
) : BaseEntity(){
    companion object{
        fun of(userId: Long): AccessLog = AccessLog(userId)
    }
}