package com.barostartbe.domain.file.usecase

import com.barostartbe.domain.file.entity.File
import com.barostartbe.domain.file.error.FileNotFoundException
import com.barostartbe.domain.file.repository.FileRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

class FileQueryUseCaseTest : DescribeSpec({

    val fileRepository = mockk<FileRepository>(relaxed = true)
    val useCase = FileQueryUseCase(fileRepository)

    beforeEach {
        clearMocks(fileRepository)
    }

    describe("FileQueryUseCase") {

        context("파일이 존재할 때") {
            val fileId = 1L
            val file = mockk<File>(relaxed = true)

            it("파일을 정상적으로 반환한다") {
                every { fileRepository.findByIdOrNull(fileId) } returns file

                val result = useCase.findById(fileId)

                result shouldBe file
            }
        }

        context("파일이 존재하지 않을 때") {
            it("FileNotFoundException을 던진다") {
                every { fileRepository.findByIdOrNull(any()) } returns null

                shouldThrow<FileNotFoundException> {
                    useCase.findById(999L)
                }
            }
        }
    }
})
