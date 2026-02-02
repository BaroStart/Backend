package com.barostartbe.domain.example.usecase

import com.barostartbe.domain.example.domain.Example
import com.barostartbe.domain.example.error.ExampleNotFoundException
import com.barostartbe.domain.example.repository.ExampleRepository
import com.barostartbe.global.annotation.QueryUseCase
import org.springframework.data.repository.findByIdOrNull

@QueryUseCase
class ExampleQueryUseCase(
    private val exampleRepository: ExampleRepository,
) {

    // ID로 예제 데이터 조회
    fun findById(id: Long): Example = exampleRepository.findByIdOrNull(id) ?: throw ExampleNotFoundException()

    // 모든 예제 데이터 조회
    fun getAll(): MutableList<Example> = exampleRepository.findAll()
}
