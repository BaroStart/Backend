package com.barostartbe.domain.example.usecase

import com.barostartbe.domain.example.domain.Example
import com.barostartbe.domain.example.error.ExampleNotFoundException
import com.barostartbe.domain.example.repository.ExampleRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class ExampleCommandUseCase(
    private val exampleRepository: ExampleRepository,
) {

    // 새로운 예제 데이터 생성
    fun create(entity: Example): Example = exampleRepository.save(entity)

    // 예제 데이터 삭제
    fun delete(id: Long) {
        val entity: Example = exampleRepository.findByIdOrNull(id) ?: throw ExampleNotFoundException()
        exampleRepository.delete(entity)
    }
}
