package com.barostartbe.domain.errorlog.entity

import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "error_log")
class ErrorLog(

    @Column(nullable = false)
    val message: String,

    @Column(nullable = false)
    val status: Int,
) : BaseEntity()
