package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.error.exception.ServiceException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.*

class AssignmentTemplateByGoalQueryUseCaseTest : DescribeSpec({

    val assignmentTemplateRepository = mockk<AssignmentTemplateRepository>(relaxed = true)
    val assignmentTemplateFileRepository = mockk<AssignmentTemplateFileRepository>(relaxed = true)
    val mentorRepository = mockk<MentorRepository>(relaxed = true)

    val useCase = AssignmentTemplateByGoalQueryUseCase(
        assignmentTemplateRepository,
        assignmentTemplateFileRepository,
        mentorRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateByGoalQueryUseCase.execute") {

        it("목표명에 해당하는 템플릿이 없으면 빈 리스트를 반환한다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true) {
                every { id } returns 1L
            }

            every {
                mentorRepository.findById(1L)
            } returns Optional.of(mentor)

            every {
                assignmentTemplateRepository
                    .findAllByMentorAndSubjectAndNameOrderByCreatedAtDesc(
                        mentor,
                        Subject.KOREAN,
                        "국어 독해"
                    )
            } returns emptyList()

            // when
            val result = useCase.execute(
                mentorId = 1L,
                subject = Subject.KOREAN,
                goalName = "국어 독해"
            )

            // then
            result.goalName shouldBe "국어 독해"
            result.templates.size shouldBe 0
        }

        it("목표명에 해당하는 템플릿과 파일이 존재하면 정상 조회된다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true) {
                every { id } returns 1L
            }

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 10L
                every { name } returns "국어 독해"
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
                mentorRepository.findById(1L)
            } returns Optional.of(mentor)

            every {
                assignmentTemplateRepository
                    .findAllByMentorAndSubjectAndNameOrderByCreatedAtDesc(
                        mentor,
                        Subject.KOREAN,
                        "국어 독해"
                    )
            } returns listOf(template)

            every {
                assignmentTemplateFileRepository.findAllByAssignmentTemplateIn(listOf(template))
            } returns listOf(file)

            // when
            val result = useCase.execute(
                mentorId = 1L,
                subject = Subject.KOREAN,
                goalName = "국어 독해"
            )

            // then
            result.templates.size shouldBe 1
            result.templates.first().files.size shouldBe 1
        }

        it("멘토가 존재하지 않으면 예외를 던진다") {
            // given
            every {
                mentorRepository.findById(any())
            } returns Optional.empty()

            // when / then
            shouldThrow<ServiceException> {
                useCase.execute(
                    mentorId = 999L,
                    subject = Subject.KOREAN,
                    goalName = "국어 독해"
                )
            }
        }
    }
})
