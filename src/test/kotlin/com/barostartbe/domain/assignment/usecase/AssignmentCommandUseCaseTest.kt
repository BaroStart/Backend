package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileUsage
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.file.usecase.FileCommandUseCase
import com.barostartbe.domain.file.usecase.FileQueryUseCase
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class AssignmentCommandUseCaseTest : DescribeSpec({

    val assignmentRepository = mockk<AssignmentRepository>(relaxed = true)
    val assignmentFileRepository = mockk<AssignmentFileRepository>(relaxed = true)
    val fileCommandUseCase = mockk<FileCommandUseCase>(relaxed = true)
    val fileQueryUseCase = mockk<FileQueryUseCase>(relaxed = true)

    val useCase = AssignmentCommandUseCase(
        assignmentRepository = assignmentRepository,
        assignmentFileRepository = assignmentFileRepository,
        fileCommandUseCase = fileCommandUseCase,
        fileQueryUseCase = fileQueryUseCase
    )

    beforeEach {
        clearMocks(
            assignmentRepository,
            assignmentFileRepository,
            fileCommandUseCase,
            fileQueryUseCase
        )
    }

    describe("AssignmentCommandUseCase") {

        context("멘티가 과제를 제출할 때") {

            val assignmentId = 1L

            val assignment = mockk<Assignment>(relaxed = true) {
                every { id } returns assignmentId
            }

            val submitReq = AssignmentSubmitReq(
                assignmentId = assignmentId,
                studyTime = 120,
                memo = "열심히 함",
                submittedAtAdjustMinutes = null,
                files = emptyList()
            )

            it("기존 제출 파일을 삭제하고 과제를 제출 상태로 변경한다") {

                every {
                    assignmentRepository.findById(assignmentId)
                } returns Optional.of(assignment)

                every {
                    assignmentFileRepository.deleteByAssignmentIdAndUsage(
                        assignmentId,
                        AssignmentFileUsage.SUBMISSION
                    )
                } just Runs

                // when
                useCase.submitAssignment(submitReq)

                // then
                verify(exactly = 1) {
                    assignmentRepository.findById(assignmentId)
                }

                verify(exactly = 1) {
                    assignmentFileRepository.deleteByAssignmentIdAndUsage(
                        assignmentId,
                        AssignmentFileUsage.SUBMISSION
                    )
                }

                // 파일이 없으므로 save는 호출되지 않음
                verify(exactly = 0) {
                    assignmentFileRepository.save(any())
                }
            }
        }
    }
})