package com.barostartbe.domain.example.usecase

import com.barostartbe.domain.example.entity.Example
import com.barostartbe.domain.example.dto.request.ExampleReq
import com.barostartbe.domain.example.error.ExampleNotFoundException
import com.barostartbe.domain.example.repository.ExampleRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull

class ExampleCommandUseCaseTest : DescribeSpec({

    val exampleRepository = mockk<ExampleRepository>(relaxed = true)
    val exampleCommandUseCase = ExampleCommandUseCase(exampleRepository)

    beforeEach {
        clearMocks(exampleRepository)
    }

    describe("ExampleCommandUseCase") {
        context("예제 데이터를 생성할 때") {
            val name = "테스트 예제"
            val exampleReq = ExampleReq(name = name)
            val savedEntity = Example(name = name)

            it("예제 데이터가 성공적으로 저장되면 ExampleRes를 반환한다") {
                every { exampleRepository.save(any()) } returns savedEntity

                val result = exampleCommandUseCase.create(exampleReq)

                result.name shouldBe name
                verify(exactly = 1) { exampleRepository.save(any()) }
            }
        }

        context("예제 데이터를 삭제할 때") {
            val exampleId = 1L
            val existingEntity = Example(name = "삭제할 예제")

            it("예제 데이터가 존재하면 성공적으로 삭제된다") {
                every { exampleRepository.findByIdOrNull(exampleId) } returns existingEntity
                every { exampleRepository.delete(existingEntity) } returns Unit

                exampleCommandUseCase.delete(exampleId)

                verify(exactly = 1) { exampleRepository.findByIdOrNull(exampleId) }
                verify(exactly = 1) { exampleRepository.delete(existingEntity) }
            }

            it("예제 데이터를 찾을 수 없으면 ExampleNotFoundException을 던진다") {
                val nonExistentId = 999L
                every { exampleRepository.findByIdOrNull(nonExistentId) } returns null

                shouldThrow<ExampleNotFoundException> {
                    exampleCommandUseCase.delete(nonExistentId)
                }

                verify(exactly = 1) { exampleRepository.findByIdOrNull(nonExistentId) }
            }
        }
    }
})
