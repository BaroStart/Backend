package com.barostartbe.domain.assignment.usecase

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enum.AssignmentFileType
import com.barostartbe.domain.assignment.entity.enum.AssignmentStatus
import com.barostartbe.domain.assignment.error.AssignmentNotFoundException
import com.barostartbe.domain.assignment.repository.AssignmentFileRepository
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.util.Optional

class AssignmentQueryUseCaseTest : DescribeSpec({

    // mocks
    val assignmentRepository = mockk<AssignmentRepository>(relaxed = true)
    val assignmentFileRepository = mockk<AssignmentFileRepository>(relaxed = true)
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
            getPreAuthenticatedUrl
        )
    }

    describe("AssignmentQueryUseCase") {

        context("과제가 존재할 때") {

            val assignmentId = 1L
            val menteeId = 100L

            val mentee = mockk<Mentee> {
                every { id } returns menteeId
            }

            val assignment = mockk<Assignment>(relaxed = true) {
                every { id } returns assignmentId
                every { this@mockk.mentee } returns mentee
                every { dueDate } returns LocalDateTime.now()
            }

            it("멘티가 과제 상세를 정상 조회한다") {

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

                // when
                val result = useCase.getAssignmentDetailByMentee(
                    assignmentId = assignmentId,
                    menteeId = menteeId
                )

                // then
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
                    useCase.getAssignmentDetailByMentee(
                        assignmentId = 999L,
                        menteeId = 1L
                    )
                }
            }
        }

        context("완료된 과제 존재 여부 확인") {
            val menteeId = 1L
            it("완료된 과제가 있으면 true를 반환한다") {
                every { assignmentRepository.existsByMentee_IdAndStatusNot(menteeId, AssignmentStatus.NOT_SUBMIT) } returns true
                useCase.checkCompletedAssignmentExists(menteeId) shouldBe true
            }

            it("완료된 과제가 없으면 false를 반환한다") {
                every { assignmentRepository.existsByMentee_IdAndStatusNot(menteeId, AssignmentStatus.NOT_SUBMIT) } returns false
                useCase.checkCompletedAssignmentExists(menteeId) shouldBe false
            }
        }

        context("7일 연속 과제 완료 스트릭 확인") {
            val menteeId = 1L
            it("스트릭이 7일 이상이면 true를 반환한다") {
                every { assignmentRepository.findMaxConsecutivePerfectDays(menteeId) } returns 7
                useCase.is7DaysAssignmentCompletedStreak(menteeId) shouldBe true
            }

            it("스트릭이 7일 미만이면 false를 반환한다") {
                every { assignmentRepository.findMaxConsecutivePerfectDays(menteeId) } returns 6
                useCase.is7DaysAssignmentCompletedStreak(menteeId) shouldBe false
            }
        }
    }
})