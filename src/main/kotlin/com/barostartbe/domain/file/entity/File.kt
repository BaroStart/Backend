package com.barostartbe.domain.file.entity

import com.barostartbe.global.common.entity.BaseFileEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "files")
class File(

    fileName: String,
    filePath: String

) : BaseFileEntity( fileName = fileName, filePath = filePath) {}