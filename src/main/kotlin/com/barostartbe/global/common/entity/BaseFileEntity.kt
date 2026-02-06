package com.barostartbe.global.common.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.ws.rs.Path

@MappedSuperclass
abstract class BaseFileEntity(

    @Column
    val fileName: String? = null,

    @Column(length = 500, nullable = false)
    val filePath: String
) : BaseEntity()
