package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateListRes
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.*

class AssignmentTemplateListQueryUseCaseTest : DescribeSpec({

    val assignmentTemplateRepository = mockk<AssignmentTemplateRepository>()
    val assignmentTemplateFileRepository = mockk<AssignmentTemplateFileRepository>()

    val useCase = AssignmentTemplateListQueryUseCase(
        assignmentTemplateRepository,
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateListQueryUseCase.execute") {

        it("과목 필터 없이 템플릿 목록을 조회한다") {
            // given
            val mentor = mockk<Mentor>()

            val template = mockk<AssignmentTemplate> {
                every { id } returns 1L
                every { subject } returns Subject.KOREAN
                every { name } returns "국어 템플릿"
                every { description } returns "설명"
            }

            val file = mockk<AssignmentTemplateFile> {
                every { assignmentTemplate } returns template
                every { fileName } returns "sample.pdf"
            }

            every {
                assignmentTemplateRepository
                    .findAllByMentorOrderByCreatedAtDesc(mentor)
            } returns listOf(template)

            every {
                assignmentTemplateFileRepository
                    .findAllByAssignmentTemplateIn(listOf(template))
            } returns listOf(file)

            // when
            val result = shouldNotThrowAny {
                useCase.execute(mentor, null)
            }

            // then
            result.size shouldBe 1
            result.first().fileNames.first() shouldBe "sample.pdf"
        }

        it("과목 필터로 템플릿 목록을 조회한다") {
            // given
            val mentor = mockk<Mentor>()

            val template = mockk<AssignmentTemplate> {
                every { id } returns 2L
                every { subject } returns Subject.MATH
                every { name } returns "수학 템플릿"
                every { description } returns "설명"
            }

            every {
                assignmentTemplateRepository
                    .findAllByMentorAndSubjectOrderByCreatedAtDesc(mentor, Subject.MATH)
            } returns listOf(template)

            every {
                assignmentTemplateFileRepository
                    .findAllByAssignmentTemplateIn(listOf(template))
            } returns emptyList()

            // when
            val result = shouldNotThrowAny {
                useCase.execute(mentor, Subject.MATH)
            }

            // then
            result.size shouldBe 1
            result.first().fileNames shouldBe emptyList()
        }
    }
})
