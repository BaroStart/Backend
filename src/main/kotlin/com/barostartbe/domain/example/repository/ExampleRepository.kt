package com.barostartbe.domain.example.repository

import com.barostartbe.domain.example.entity.Example
import org.springframework.data.jpa.repository.JpaRepository

interface ExampleRepository : JpaRepository<Example, Long> {
}
