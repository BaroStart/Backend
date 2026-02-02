package com.barostartbe.domain.example.usecase

import com.barostartbe.domain.example.entity.Example
import com.barostartbe.domain.example.error.ExampleNotFoundException
import com.barostartbe.domain.example.repository.ExampleRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull

class ExampleQueryUseCaseTest : DescribeSpec({

    val exampleRepository = mockk<ExampleRepository>(relaxed = true)
    val exampleQueryUseCase = ExampleQueryUseCase(exampleRepository)

    beforeEach {
        clearMocks(exampleRepository)
    }

    describe("ExampleQueryUseCase") {
        context("ID로 예제 데이터를 조회할 때") {
            val exampleId = 1L
            val name = "조회할 예제"
            val expectedEntity = Example(name = name)

            it("예제 데이터가 존재하면 해당 데이터를 반환한다") {
                every { exampleRepository.findByIdOrNull(exampleId) } returns expectedEntity

                val result = exampleQueryUseCase.findById(exampleId)

                result.name shouldBe name
                verify(exactly = 1) { exampleRepository.findByIdOrNull(exampleId) }
            }

            it("예제 데이터를 찾을 수 없으면 ExampleNotFoundException을 던진다") {
                val nonExistentId = 999L
                every { exampleRepository.findByIdOrNull(nonExistentId) } returns null

                shouldThrow<ExampleNotFoundException> {
                    exampleQueryUseCase.findById(nonExistentId)
                }

                verify(exactly = 1) { exampleRepository.findByIdOrNull(nonExistentId) }
            }
        }

        context("모든 예제 데이터를 조회할 때") {
            it("여러 예제 데이터가 존재하면 모든 데이터를 반환한다") {
                val exampleList = mutableListOf(
                    Example(name = "예제1"),
                    Example(name = "예제2"),
                    Example(name = "예제3")
                )
                every { exampleRepository.findAll() } returns exampleList

                val result = exampleQueryUseCase.getAll()

                result shouldHaveSize 3
                result[0].name shouldBe "예제1"
                result[1].name shouldBe "예제2"
                result[2].name shouldBe "예제3"
                verify(exactly = 1) { exampleRepository.findAll() }
            }

            it("예제 데이터가 없으면 빈 리스트를 반환한다") {
                every { exampleRepository.findAll() } returns mutableListOf()

                val result = exampleQueryUseCase.getAll()

                result shouldHaveSize 0
                verify(exactly = 1) { exampleRepository.findAll() }
            }
        }
    }
})
