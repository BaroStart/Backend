package com.barostartbe.domain.example.usecase

import com.barostartbe.domain.example.dto.response.ExampleRes
import com.barostartbe.domain.example.error.ExampleNotFoundException
import com.barostartbe.domain.example.repository.ExampleRepository
import com.barostartbe.global.annotation.QueryUseCase
import org.springframework.data.repository.findByIdOrNull

@QueryUseCase
class ExampleQueryUseCase(
    private val exampleRepository: ExampleRepository,
) {

    // ID로 예제 데이터 조회
    fun findById(id: Long): ExampleRes {
        val entity = exampleRepository.findByIdOrNull(id) ?: throw ExampleNotFoundException()
        return ExampleRes.from(entity)
    }

    // 모든 예제 데이터 조회
    fun getAll(): MutableList<ExampleRes> = exampleRepository.findAll()
        .map { ExampleRes.from(it) }
        .toMutableList()
}
