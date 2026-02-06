package com.barostartbe.domain.file.repository

import com.barostartbe.domain.file.entity.File
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long>