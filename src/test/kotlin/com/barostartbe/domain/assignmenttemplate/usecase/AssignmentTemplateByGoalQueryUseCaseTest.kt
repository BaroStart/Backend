package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.AssignmentGoal
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.repository.AssignmentGoalRepository
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.global.error.exception.ServiceException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.*

class AssignmentTemplateByGoalQueryUseCaseTest : DescribeSpec({

    val assignmentGoalRepository = mockk<AssignmentGoalRepository>(relaxed = true)
    val assignmentTemplateRepository = mockk<AssignmentTemplateRepository>(relaxed = true)
    val assignmentTemplateFileRepository = mockk<AssignmentTemplateFileRepository>(relaxed = true)

    val useCase = AssignmentTemplateByGoalQueryUseCase(
        assignmentGoalRepository,
        assignmentTemplateRepository,
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateByGoalQueryUseCase.execute") {

        it("목표에 템플릿이 없는 경우 빈 리스트를 반환한다") {
            // given
            val goal = mockk<AssignmentGoal>(relaxed = true) {
                every { id } returns 1L
                every { name } returns "국어 독해"
                every { subject } returns Subject.KOREAN
            }

            every {
                assignmentGoalRepository.findById(1L)
            } returns Optional.of(goal)

            every {
                assignmentTemplateRepository.findAllByAssignmentGoalOrderByCreatedAtDesc(goal)
            } returns emptyList()

            // when
            val result = useCase.execute(1L)

            // then
            result.goalId shouldBe 1L
            result.templates.size shouldBe 0
        }

        it("목표에 템플릿과 파일이 존재하면 정상 조회된다") {
            // given
            val goal = mockk<AssignmentGoal>(relaxed = true) {
                every { id } returns 1L
                every { name } returns "국어 독해"
                every { subject } returns Subject.KOREAN
            }

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 10L
                every { name } returns "독해 템플릿"
                every { description } returns "설명"
                every { title } returns "제목"
                every { content } returns "내용"
            }

            val file = mockk<AssignmentTemplateFile>(relaxed = true) {
                every { id } returns 100L
                every { assignmentTemplate } returns template
                every { fileName } returns "file.pdf"
                every { url } returns "url"
            }

            every {
                assignmentGoalRepository.findById(1L)
            } returns Optional.of(goal)

            every {
                assignmentTemplateRepository.findAllByAssignmentGoalOrderByCreatedAtDesc(goal)
            } returns listOf(template)

            every {
                assignmentTemplateFileRepository.findAllByAssignmentTemplateIn(listOf(template))
            } returns listOf(file)

            // when
            val result = useCase.execute(1L)

            // then
            result.goalId shouldBe 1L
            result.templates.size shouldBe 1
            result.templates.first().files.size shouldBe 1
        }

        it("목표가 존재하지 않으면 예외를 던진다") {
            // given
            every {
                assignmentGoalRepository.findById(any())
            } returns Optional.empty()

            // when / then
            shouldThrow<ServiceException> {
                useCase.execute(999L)
            }
        }
    }
})
