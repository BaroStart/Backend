package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.file.usecase.FileQueryUseCase
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class AssignmentQueryUseCaseTest : DescribeSpec({

    // mocks
    val assignmentRepository = mockk<AssignmentRepository>(relaxed = true)
    val assignmentFileRepository = mockk<AssignmentFileRepository>(relaxed = true)
    val fileQueryUseCase = mockk<FileQueryUseCase>(relaxed = true)
    val getPreAuthenticatedUrl = mockk<GetPreAuthenticatedUrl>(relaxed = true)

    // useCase
    val useCase = AssignmentQueryUseCase(
        assignmentRepository = assignmentRepository,
        assignmentFileRepository = assignmentFileRepository,
        getPreAuthenticatedUrl = getPreAuthenticatedUrl
    )

    beforeEach {
        clearMocks(
            assignmentRepository,
            assignmentFileRepository,
            fileQueryUseCase,
            getPreAuthenticatedUrl
        )
    }

    describe("AssignmentQueryUseCase") {

        context("과제가 존재할 때") {

            val assignmentId = 1L
            val assignment = mockk<Assignment>(relaxed = true) {
                every { id } returns assignmentId
            }

            it("과제 상세를 정상 조회한다") {

                every {
                    assignmentRepository.findById(assignmentId)
                } returns Optional.of(assignment)

                every {
                    assignmentFileRepository.findAllByAssignmentIdAndFileType(
                        assignmentId,
                        AssignmentFileType.MATERIAL
                    )
                } returns emptyList()

                every {
                    assignmentFileRepository.findAllByAssignmentIdAndFileType(
                        assignmentId,
                        AssignmentFileType.SUBMISSION
                    )
                } returns emptyList()

                val result = useCase.getAssignmentDetail(assignmentId)

                result.assignmentId shouldBe assignmentId
                result.materials.size shouldBe 0
                result.submissions.size shouldBe 0
            }
        }

        context("과제가 존재하지 않을 때") {

            it("AssignmentNotFoundException을 던진다") {

                every {
                    assignmentRepository.findById(any())
                } returns Optional.empty()

                shouldThrow<AssignmentNotFoundException> {
                    useCase.getAssignmentDetail(999L)
                }
            }
        }
    }
})
