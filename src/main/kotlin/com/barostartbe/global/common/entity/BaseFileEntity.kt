package com.barostartbe.global.common.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseFileEntity(

    @Column
    val fileName: String? = null,

    @Column(length = 500)
    val url: String? = null,
) : BaseEntity()
