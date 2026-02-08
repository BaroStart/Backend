package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.error.exception.ServiceException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*

class AssignmentTemplateDetailQueryUseCaseTest : DescribeSpec({

    val assignmentTemplateRepository =
        mockk<AssignmentTemplateRepository>(relaxed = true)
    val assignmentTemplateFileRepository =
        mockk<AssignmentTemplateFileRepository>(relaxed = true)

    val useCase = AssignmentTemplateDetailQueryUseCase(
        assignmentTemplateRepository,
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateDetailQueryUseCase.execute") {

        it("멘토가 자신의 템플릿 상세를 정상 조회한다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 1L
                every { subject } returns Subject.KOREAN
                every { name } returns "국어 독해"
                every { description } returns "설명"
                every { title } returns "제목"
                every { content } returns "내용"
            }

            val file = mockk<AssignmentTemplateFile>(relaxed = true) {
                every { fileName } returns "file.pdf"
                every { url } returns "url"
            }

            every {
                assignmentTemplateRepository.findByIdAndMentor(1L, mentor)
            } returns template

            every {
                assignmentTemplateFileRepository.findAllByAssignmentTemplate(template)
            } returns listOf(file)

            // when
            val result = useCase.execute(mentor, 1L)

            // then
            result.id shouldBe 1L
            result.files.size shouldBe 1
        }

        it("파일이 없어도 정상 조회된다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 2L
                every { subject } returns Subject.MATH
                every { name } returns "수학"
                every { description } returns ""
                every { title } returns "제목"
                every { content } returns ""
            }

            every {
                assignmentTemplateRepository.findByIdAndMentor(2L, mentor)
            } returns template

            every {
                assignmentTemplateFileRepository.findAllByAssignmentTemplate(template)
            } returns emptyList()

            // when
            val result = useCase.execute(mentor, 2L)

            // then
            result.id shouldBe 2L
            result.files.size shouldBe 0
        }

        it("템플릿이 없으면 예외를 던진다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            every {
                assignmentTemplateRepository.findByIdAndMentor(any(), mentor)
            } returns null

            // when / then
            shouldThrow<ServiceException> {
                useCase.execute(mentor, 999L)
            }
        }
    }
})
